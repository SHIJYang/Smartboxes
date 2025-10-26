package org.example.boxes.service;

import java.util.List;
import org.example.boxes.dto.EmotionDTO;
import org.example.boxes.dto.QueryEmotionDTO;
import org.example.boxes.result.RestResult;

/**
 * 情感标签服务接口
 *
 * @author 14577
 */
public interface EmotionService {

    /**
     * 添加新的情感标签
     *
     * @param emotionDTO 情感数据传输对象
     * @return RestResult 结果封装
     */
    RestResult<Void> addEmotion(EmotionDTO emotionDTO);

    /**
     * 删除情感标签记录
     *
     * @param id 情感记录主键ID
     * @return RestResult 结果封装
     */
    RestResult<Void> deleteEmotion(Long id);

    /**
     * 更新情感标签信息
     *
     * @param emotionDTO 更新的情感数据
     * @return RestResult 结果封装
     */
    RestResult<Void> updateEmotion(EmotionDTO emotionDTO);

    /**
     * 查询情感标签列表
     *
     * @param queryEmotionDTO 查询条件参数
     * @return RestResult 结果封装
     */
    RestResult<List<EmotionDTO>> listEmotions(QueryEmotionDTO queryEmotionDTO);
}