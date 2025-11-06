// 1. 导入工具
import mockUtils from '@/mock/api';
import http from '@/utils/request';

// 2. 环境判断
const USE_MOCK = process.env.NODE_ENV === 'development';

// 辅助函数：生成随机位置
const getRandomLocation = () => {
  const locations = ['书房', '卧室', '客厅', '厨房', '衣帽间', '阳台', '卫生间'];
  return locations[Math.floor(Math.random() * locations.length)];
};

// 辅助函数：转换收纳盒数据格式
const transformBox = (box) => ({
  id: box.id,
  name: box.box_name || '未命名',
  category: 'box',
  itemCount: Math.floor(Math.random() * 10) + 1, // 模拟数据
  location: getRandomLocation(),
  lastModified: box.last_heartbeat_time,
  batteryLevel: box.battery,
  isCharging: box.battery < 50,
  ...box
});

/**
 * 获取收纳盒列表
 * @returns {Promise<Array>} 转换后的收纳盒数组
 */
export const getBoxes = async () => {
  if (USE_MOCK) {
    const mockRes = mockUtils.getBoxes();
    return Array.isArray(mockRes?.data) 
      ? mockRes.data.map(transformBox) 
      : [];
  }
  const realRes = await http.get('/api/boxes', {}, {
    showLoading: true,
    loadingText: '获取收纳盒信息中...'
  });
  return Array.isArray(realRes) ? realRes.map(transformBox) : [];
};

/**
 * 获取单个收纳盒详情
 * @param {number} id - 收纳盒ID
 * @returns {Promise<Object>} 收纳盒详情
 */
export const getBoxById = async (id) => {
  if (USE_MOCK) {
    const mockRes = mockUtils.getBoxes();
    const box = mockRes.data.find(item => item.id === id);
    return box ? transformBox(box) : null;
  }
  const realRes = await http.get(`/api/boxes/${id}`, {}, {
    showLoading: true,
    loadingText: '获取收纳盒详情中...'
  });
  return realRes ? transformBox(realRes) : null;
};

/**
 * 新增收纳盒
 * @param {Object} boxData - 收纳盒数据
 * @param {string} boxData.box_name - 收纳盒名称
 * @param {number} boxData.box_type - 收纳盒类型
 * @returns {Promise<Object>} 新增结果
 */
export const addBox = async (boxData) => {
  if (USE_MOCK) {
    const newBox = {
      id: Date.now(), // 模拟ID
      box_code: `ESP32-CAM-${new Date().getTime()}`,
      user_id: 1,
      ...boxData,
      status: 1,
      battery: 100,
      last_heartbeat_time: new Date().toISOString(),
      create_time: new Date().toISOString(),
      update_time: new Date().toISOString()
    };
    mockUtils.getBoxes().data.push(newBox); // 模拟存入Mock数据
    return { code: 200, data: transformBox(newBox) };
  }
  return await http.post('/api/boxes', boxData, {
    showLoading: true,
    loadingText: '添加收纳盒中...'
  });
};

/**
 * 更新收纳盒
 * @param {number} id - 收纳盒ID
 * @param {Object} boxData - 待更新数据
 * @returns {Promise<Object>} 更新结果
 */
export const updateBox = async (id, boxData) => {
  if (USE_MOCK) {
    const boxes = mockUtils.getBoxes().data;
    const index = boxes.findIndex(item => item.id === id);
    if (index > -1) {
      boxes[index] = { ...boxes[index], ...boxData, update_time: new Date().toISOString() };
      return { code: 200, data: transformBox(boxes[index]) };
    }
    return { code: 404, message: '收纳盒不存在' };
  }
  return await http.put(`/api/boxes/${id}`, boxData, {
    showLoading: true,
    loadingText: '更新收纳盒中...'
  });
};

/**
 * 删除收纳盒
 * @param {number} id - 收纳盒ID
 * @returns {Promise<Object>} 删除结果
 */
export const deleteBox = async (id) => {
  if (USE_MOCK) {
    const boxes = mockUtils.getBoxes().data;
    const initialLength = boxes.length;
    mockUtils.getBoxes().data = boxes.filter(item => item.id !== id);
    return { 
      code: 200, 
      data: { success: mockUtils.getBoxes().data.length < initialLength }
    };
  }
  return await http.delete(`/api/boxes/${id}`, {}, {
    showLoading: true,
    loadingText: '删除收纳盒中...'
  });
};

export default {
  getBoxes,
  getBoxById,
  addBox,
  updateBox,
  deleteBox
};