package cn.junbao.yvbao.live.id.generate.service.impl;

import cn.junbao.yvbao.live.id.generate.dao.mapper.IdGenerateMapper;
import cn.junbao.yvbao.live.id.generate.dao.po.IdGeneratePO;
import cn.junbao.yvbao.live.id.generate.service.IdGenerateService;
import cn.junbao.yvbao.live.id.generate.service.bo.LocalSeqIdBO;
import cn.junbao.yvbao.live.id.generate.service.bo.LocalUnSeqIdBO;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerateServiceImpl implements IdGenerateService, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(IdGenerateServiceImpl.class);
    @Resource
    private IdGenerateMapper idGenerateMapper;
    private static final Map<Integer,LocalSeqIdBO> localSeqIdBOMap = new ConcurrentHashMap<>();
    private static Map<Integer, LocalUnSeqIdBO> localUnSeqIdBOMap = new ConcurrentHashMap<>();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt(1000));
                    return thread;
                }
            });
    private static final float UPDATE_RATE = 0.75f;
    private static  Map<Integer, Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    private static final int SEQ_ID = 1;

    @Override
    public Long getUnSeqId(Integer id) {
        if (id == null) {
            log.error("[getSeqId] id is error,id is {}", id);
            return null;
        }
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdBOMap.get(id);
        if (localUnSeqIdBO == null) {
            log.error("[getUnSeqId] localUnSeqIdBO is null,id is {}", id);
            return null;
        }
        Long returnId = localUnSeqIdBO.getIdQueue().poll();
        if (returnId == null) {
            log.error("[getUnSeqId] returnId is null,id is {}", id);
            return null;
        }
        this.refreshLocalUnSeqId(localUnSeqIdBO);
        return returnId;
    }

    @Override
    public Long getSeqId(Integer id) {
        if (id == null ){
            log.error("[getSeqId] id is null !!! error");
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdBOMap.get(id);
        if (localSeqIdBO == null ){
            log.error("[getSeqId] localSeqIdBO is null , id is {}",id);
            return null;
        }
        this.refreshLocalSeqId(localSeqIdBO);
        long resultId = localSeqIdBO.getCurrentNum().incrementAndGet();

        if (resultId > localSeqIdBO.getNextThreshold()) {
            //超出当前范围, 同步去刷新
            log.error("[getSeqId] id is over limit,id is {}", id);
            return null;
        }
        return resultId;
    }

    /**
     * 更新扩容当前的 有序id序列 // todo 数据库库表建立 + 方法测试   1/10  18:21
     * @param localSeqIdBO
     */
    private void refreshLocalSeqId(LocalSeqIdBO localSeqIdBO) {
        long step = localSeqIdBO.getNextThreshold() - localSeqIdBO.getCurrentStart();
        if ((localSeqIdBO.getCurrentNum().get() - localSeqIdBO.getCurrentStart()) > step * UPDATE_RATE){
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            if (semaphore == null ){
                log.error("[refreshLocalSeqId] semaphore is null !!! id is {}",localSeqIdBO.getId());
            }

            boolean acquireStatus = semaphore.tryAcquire();
            if (acquireStatus){
                log.info("[refreshLocalSeqId] 开始进行本地id段的同步操作");
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
                            tryUpdateDBRecord(idGeneratePO);
                        } catch (Exception e) {
                            log.error("[refreshLocalSeqId] error is  " , e);
                            throw new RuntimeException(e);
                        }finally {
                            semaphoreMap.get(localSeqIdBO.getId()).release();
                            log.info("本地有序id段同步完成,id is {}", localSeqIdBO.getId());
                        }
                    }
                });
            }
        }
    }

    /**
     * 刷新本地无序id段
     *
     * @param localUnSeqIdBO
     */
    private void refreshLocalUnSeqId(LocalUnSeqIdBO localUnSeqIdBO) {
        long begin = localUnSeqIdBO.getCurrentStart();
        long end = localUnSeqIdBO.getNextThreshold();
        long remainSize = localUnSeqIdBO.getIdQueue().size();
        //如果使用剩余空间不足25%，则进行刷新
        if ((end - begin) * 0.25 > remainSize) {
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBO.getId());
            if (semaphore == null) {
                log.error("semaphore is null,id is {}", localUnSeqIdBO.getId());
                return;
            }
            boolean acquireStatus = semaphore.tryAcquire();
            if (acquireStatus) {
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
                            tryUpdateDBRecord(idGeneratePO);
                        } catch (Exception e) {
                            log.error("[refreshLocalUnSeqId] error is ", e);
                        } finally {
                            semaphoreMap.get(localUnSeqIdBO.getId()).release();
                            log.info("本地无序id段同步完成，id is {}", localUnSeqIdBO.getId());
                        }
                    }
                });
            }
        }
    }


    /**
     * bean初始化之后会进行调用该方法
     * 初始化 map 集合
     * @throws Exception
     */

    @Override
    public void afterPropertiesSet() throws Exception {
        // select all 初始化所有的id生成策略
        List<IdGeneratePO> idGeneratePOS = idGenerateMapper.selectAll();
        for (IdGeneratePO idGeneratePO : idGeneratePOS) {
            tryUpdateDBRecord(idGeneratePO);
            //初始化 信号量semaphoreMap
            semaphoreMap.put(idGeneratePO.getId(), new Semaphore(1));
        }
    }


    /**
     * 更新mysql里面的分布式id的配置信息，占用相应的id段
     * 同步执行，很多的网络IO，性能较慢
     *
     * @param idGeneratePO
     */
    private void tryUpdateDBRecord(IdGeneratePO idGeneratePO){

        int updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());

        if (updateResult > 0 ){
            localIdBOHandler(idGeneratePO);
            return;
        }
        //重试进行更新
        for (int i = 0; i < 3; i++) {
            idGeneratePO = idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
            if (updateResult > 0) {
                localIdBOHandler(idGeneratePO);
                return;
            }
        }
        throw new RuntimeException("[tryUpdateDBRecord] 表id段占用失败，竞争过于激烈，id is " + idGeneratePO.getId());
    }



    /**
     * 专门处理如何将本地ID对象放入到Map中，并且进行初始化的
     *
     * @param idGeneratePO
     */
    private void localIdBOHandler(IdGeneratePO idGeneratePO) {
        long currentStart = idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        long currentNum = currentStart;
        if (idGeneratePO.getIsSeq() == SEQ_ID) {
            LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
            AtomicLong atomicLong = new AtomicLong(currentNum);
            localSeqIdBO.setId(idGeneratePO.getId());
            localSeqIdBO.setCurrentNum(atomicLong);
            localSeqIdBO.setCurrentStart(currentStart);
            localSeqIdBO.setNextThreshold(nextThreshold);
            localSeqIdBOMap.put(localSeqIdBO.getId(), localSeqIdBO);
        } else {
            LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
            localUnSeqIdBO.setCurrentStart(currentStart);
            localUnSeqIdBO.setNextThreshold(nextThreshold);
            localUnSeqIdBO.setId(idGeneratePO.getId());
            long begin = localUnSeqIdBO.getCurrentStart();
            long end = localUnSeqIdBO.getNextThreshold();
            List<Long> idList = new ArrayList<>();
            for (long i = begin; i < end; i++) {
                idList.add(i);
            }
            //将本地id段提前打乱，然后放入到队列中
            Collections.shuffle(idList);
            ConcurrentLinkedQueue<Long> idQueue = new ConcurrentLinkedQueue<>();
            idQueue.addAll(idList);
            localUnSeqIdBO.setIdQueue(idQueue);
            localUnSeqIdBOMap.put(localUnSeqIdBO.getId(), localUnSeqIdBO);
        }
    }
}
