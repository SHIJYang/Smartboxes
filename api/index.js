// 1. 导入工具：区分 Mock 函数和真实请求拦截器
import { 
  getItems as mockGetItems, 
  getBoxes as mockGetBoxes, 
  getUserInfo as mockGetUserInfo, 
  postChat as mockPostChat 
} from '@/mock/api';
import http from '@/utils/request'; // 导入之前封装的真实请求拦截器

// 2. 环境判断：开发环境用 Mock，生产环境用真实接口（统一开关）
const USE_MOCK = process.env.NODE_ENV === 'development'; // 开发环境默认用 Mock

/**
 * 统一处理返回格式：确保 Mock 和真实接口返回结构一致（仅返回业务数据层）
 * @param {Object} res - 接口响应（Mock 或真实接口返回）
 * @returns {Object} 业务数据（res.data.data 或 res.data）
 */
const handleResponse = (res) => {
  // 兼容 Mock 直接返回的结构（{ code, data }）和真实接口的嵌套结构（{ code, data: { ... } }）
  if (res && res.code === 200) {
    // 若 data 内还有 data 层（真实接口常见格式），则返回内层 data；否则返回外层 data
    return res.data?.data || res.data;
  }
  // 非成功响应（如 Mock 错误模拟、真实接口错误），抛出错误供业务层捕获
  throw new Error(res?.message || '接口请求失败');
};


// 4. 收纳盒列表接口：无参数，统一返回格式
export const getBoxes = async () => {
  if (USE_MOCK) {
    const mockRes = mockGetBoxes();
    return handleResponse(mockRes);
  }
  const realRes = await http.get('/api/boxes', {}, {
    showLoading: true,
    loadingText: '获取收纳盒信息中...'
  });
  return realRes;
};

// 5. 用户信息接口：无参数，适配嵌套返回格式
export const getUserInfo = async () => {
  if (USE_MOCK) {
    const mockRes = mockGetUserInfo();
    return handleResponse(mockRes);
  }
  const realRes = await http.get('/api/user/info', {}, {
    showLoading: false, // 用户信息通常在页面加载时获取，可隐藏加载中
    withToken: true // 需携带 Token（默认 true，可省略）
  });
  return realRes;
};

// 6. 聊天发送接口：POST 请求，处理请求体参数
export const sendChatMessage = async (data = {}) => {
  // 确保传入 message 参数（避免 Mock 和真实接口参数缺失）
  if (!data.message) {
    throw new Error('请传入聊天消息内容（message）');
  }

  if (USE_MOCK) {
    // Mock 环境：调用 Mock 函数（需传入 message，与真实接口参数一致）
    const mockRes = mockPostChat(data.message);
    return handleResponse(mockRes);
  }
  // 真实环境：POST 请求，data 作为请求体
  const realRes = await http.post('/api/chat', data, {
    showLoading: true,
    loadingText: '发送中...'
  });
  return realRes;
};