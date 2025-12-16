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
          resolve(res.data as RestResult<T>);
        } else if (res.statusCode === 401) {
          // 未授权，跳转到登录页
          uni.showToast({ title: '请重新登录', icon: 'none' });
          uni.removeStorageSync('userInfo');
          uni.redirectTo({ url: '/pages/user/login' });
          reject(res);
        } else {
          uni.showToast({ title: res.data?.message || '请求异常', icon: 'none' });
          reject(res);
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      },
      complete: () => {
        // 请求完成后的处理
        console.log(`${method} ${url} 请求完成`);
      }
    });
  });
};