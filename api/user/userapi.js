// userapi.js
// 1. 导入工具：区分 Mock 函数和真实请求拦截器
import mockUtils from '@/mock/api';
import http from '@/utils/request';

// 2. 环境判断：开发环境用 Mock，生产环境用真实接口
const USE_MOCK = process.env.NODE_ENV === 'development';

/**
 * 获取用户基本信息
 * @returns {Promise<Object>} 用户信息对象
 */
export const getUserInfo = async () => {
  if (USE_MOCK) {
    // Mock环境：处理用户信息格式
    const mockRes = mockUtils.getUserInfo();
    if (mockRes.code === 200 && mockRes.data) {
      return {
        id: mockRes.data.id,
        account: mockRes.data.user_account,
        nickname: mockRes.data.username,
        phone: mockRes.data.phone,
        joinTime: mockRes.data.create_time,
        lastUpdateTime: mockRes.data.update_time
      };
    }
    return null;
  }
  // 真实环境：调用用户信息接口
  const realRes = await http.get('/api/user/info', {}, {
    showLoading: false, // 用户信息通常静默加载
    loadingText: '获取用户信息中...'
  });
  return realRes || null;
};

/**
 * 更新用户信息
 * @param {Object} userData - 待更新的用户数据
 * @param {string} [userData.username] - 用户名
 * @param {string} [userData.phone] - 手机号
 * @returns {Promise<Object>} 更新结果
 */
export const updateUserInfo = async (userData) => {
  if (USE_MOCK) {
    // Mock环境：模拟更新成功
    return {
      code: 200,
      message: '更新成功',
      data: {
        ...MOCK_DATA.USER_INFO.data,
        ...userData,
        update_time: new Date().toISOString()
      }
    };
  }
  // 真实环境：调用更新接口
  const realRes = await http.put('/api/user/info', userData, {
    showLoading: true,
    loadingText: '更新用户信息中...'
  });
  return realRes;
};

export default {
  getUserInfo,
  updateUserInfo
};