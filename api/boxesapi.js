// 1. 导入工具：区分 Mock 函数和真实请求拦截器
import mockUtils from '@/mock/api';
import http from '@/utils/request'; // 导入之前封装的真实请求拦截器

// 2. 环境判断：开发环境用 Mock，生产环境用真实接口（统一开关）
const USE_MOCK = process.env.NODE_ENV === 'development'; // 开发环境默认用 Mock


// 辅助函数：生成随机位置
const getRandomLocation = () => {
  const locations = ['书房', '卧室', '客厅', '厨房', '衣帽间', '阳台', '卫生间'];
  return locations[Math.floor(Math.random() * locations.length)];
};
//收纳盒列表接口：无参数，统一返回格式
export  const getBoxes = async () => {
	
  if (USE_MOCK) {
    const mockRes = mockUtils.getBoxes();
	console.log('Mock数据:', mockRes);
    const transformedData = Array.isArray(mockRes?.data) 
          ? mockRes.data.map(box => ({
              id: box.id,
              name: box.boxName || '未命名',
              category: 'box', // 默认分类
              itemCount: Math.floor(Math.random() * 10) + 1, // 模拟物品数量
              location: getRandomLocation(), // 模拟位置
              lastModified: box.lastHeartbeatTime,
              batteryLevel: box.battery,
              isCharging: box.battery < 50, // 电量低于50%模拟充电
              // 保留原始字段
              ...box
            }))
          : [];
        
        return transformedData;
      
  }
  const realRes = await http.get('/api/boxes', {}, {
    showLoading: true,
    loadingText: '获取收纳盒信息中...'
  });
  return Array.isArray(realRes?.data) ? realRes.data : Array.isArray(realRes) ? realRes : [];;
};
export default {
    getBoxes
}
