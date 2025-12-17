import config from '@/common/config';
import type { RestResult } from '@/common/types';

export const request = <T>(
  url: string,
  method: 'GET' | 'POST' | 'PUT' | 'DELETE',
  data: any = {},
  mockSource?: RestResult<T>
): Promise<RestResult<T>> => {
  return new Promise((resolve, reject) => {
    // 1. Mock 拦截
    if (config.useMock && mockSource) {
      console.log(`[Mock] ${method} ${url}`, data);
      setTimeout(() => {
        resolve(mockSource);
      }, config.mockDelay);
      return;
    }

    // 2. 真实请求
    uni.request({
      url: config.baseUrl + url,
      method: method,
      data: data,
      header: { 
        'Authorization': uni.getStorageSync('token') || '',
        'Content-Type': 'application/json'
      },
      success: (res: any) => {
        if (res.statusCode === 200) {
          // 修复: 后端返回字段可能包含 msg 而非 message，这里做兼容处理
          const result = res.data as RestResult<T>;
          // 如果后端有时返回 message 有时返回 msg，可以手动规整一下 (可选)
          // if (!result.msg && (result as any).message) result.msg = (result as any).message;
          
          resolve(result);
        } else if (res.statusCode === 401) {
          uni.showToast({ title: '请重新登录', icon: 'none' });
          uni.removeStorageSync('userInfo');
          uni.redirectTo({ url: '/pages/user/login' });
          reject(res);
        } else {
          // 修复: 优先取 msg
          const errorMsg = res.data?.msg || res.data?.message || '请求异常';
          uni.showToast({ title: errorMsg, icon: 'none' });
          reject(res);
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      },
      complete: () => {
        console.log(`${method} ${url} 请求完成`);
      }
    });
  });
};