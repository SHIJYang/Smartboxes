// 1. 导入工具
import mockUtils from '@/mock/api';
import http from '@/utils/request';

// 2. 环境判断
const USE_MOCK = process.env.NODE_ENV === 'development';

// 辅助函数：转换物品数据格式
const transformItem = (item) => ({
  id: item.id,
  code: item.item_code,
  name: item.manual_edit_name || item.auto_recognize_name || '未知物品',
  boxId: item.box_id,
  tag: item.item_tag,
  description: item.item_desc,
  putInTime: item.put_in_time,
  expireTime: item.expire_time,
  isValid: item.is_valid === 1,
  ...item
});

/**
 * 获取物品列表（支持分页）
 * @param {Object} params - 分页参数
 * @param {number} params.page - 页码
 * @param {number} params.pageSize - 每页条数
 * @returns {Promise<Object>} 物品列表及总数
 */
export const getItems = async (params = {}) => {
  if (USE_MOCK) {
    const mockRes = mockUtils.getItems(params);
    return {
      ...mockRes,
      data: mockRes.data.map(transformItem)
    };
  }
  const realRes = await http.get('/api/items', params, {
    showLoading: true,
    loadingText: '获取物品列表中...'
  });
  return {
    ...realRes,
    data: realRes.data.map(transformItem)
  };
};

/**
 * 获取单个物品详情
 * @param {number} id - 物品ID
 * @returns {Promise<Object>} 物品详情
 */
export const getItemById = async (id) => {
  if (USE_MOCK) {
    const allItems = mockUtils.getItems({ page: 1, pageSize: 100 }).data; // 获取全部物品
    const item = allItems.find(item => item.id === id);
    return item ? transformItem(item) : null;
  }
  const realRes = await http.get(`/api/items/${id}`, {}, {
    showLoading: true,
    loadingText: '获取物品详情中...'
  });
  return realRes ? transformItem(realRes) : null;
};

/**
 * 新增物品
 * @param {Object} itemData - 物品数据
 * @param {string} itemData.item_code - 物品编码
 * @param {number} itemData.box_id - 所属收纳盒ID
 * @returns {Promise<Object>} 新增结果
 */
export const addItem = async (itemData) => {
  if (USE_MOCK) {
    const newItem = {
      id: Date.now(), // 模拟ID
      ...itemData,
      is_valid: 1,
      create_time: new Date().toISOString(),
      update_time: new Date().toISOString()
    };
    MOCK_DATA.ITEMS.push(newItem); // 存入Mock数据
    return { code: 200, data: transformItem(newItem) };
  }
  return await http.post('/api/items', itemData, {
    showLoading: true,
    loadingText: '添加物品中...'
  });
};

/**
 * 更新物品
 * @param {number} id - 物品ID
 * @param {Object} itemData - 待更新数据
 * @returns {Promise<Object>} 更新结果
 */
export const updateItem = async (id, itemData) => {
  if (USE_MOCK) {
    const index = MOCK_DATA.ITEMS.findIndex(item => item.id === id);
    if (index > -1) {
      MOCK_DATA.ITEMS[index] = { ...MOCK_DATA.ITEMS[index], ...itemData, update_time: new Date().toISOString() };
      return { code: 200, data: transformItem(MOCK_DATA.ITEMS[index]) };
    }
    return { code: 404, message: '物品不存在' };
  }
  return await http.put(`/api/items/${id}`, itemData, {
    showLoading: true,
    loadingText: '更新物品中...'
  });
};

/**
 * 删除物品
 * @param {number} id - 物品ID
 * @returns {Promise<Object>} 删除结果
 */
export const deleteItem = async (id) => {
  if (USE_MOCK) {
    const initialLength = MOCK_DATA.ITEMS.length;
    MOCK_DATA.ITEMS = MOCK_DATA.ITEMS.filter(item => item.id !== id);
    return { 
      code: 200, 
      data: { success: MOCK_DATA.ITEMS.length < initialLength }
    };
  }
  return await http.delete(`/api/items/${id}`, {}, {
    showLoading: true,
    loadingText: '删除物品中...'
  });
};

export default {
  getItems,
  getItemById,
  addItem,
  updateItem,
  deleteItem
};