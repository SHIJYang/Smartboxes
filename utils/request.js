/**
 * UniApp Request 拦截器封装
 * 注意：UniApp 中无需导入 uni-app，直接使用全局 uni 对象调用 API
 */

// 1. 移除从 "uni-app" 的导入，直接使用全局 uni 对象
// 原错误代码：import { getStorageSync, removeStorageSync, showToast, showModal, getSystemInfoSync } from "uni-app";


// 1. 创建请求取消令牌管理器
const cancelTokenMap = new Map();

/**
 * 生成请求唯一标识
 */
const generateReqKey = (url, method, data = {}) => {
  const sortedData = Object.keys(data).sort().reduce((obj, key) => {
    obj[key] = data[key];
    return obj;
  }, {});
  return `${url}-${method.toUpperCase()}-${JSON.stringify(sortedData)}`;
};

/**
 * 取消指定请求
 */
const cancelRequest = (url, method, data = {}) => {
  if (url && method) {
    const fullUrl = `${http.baseURL.replace(/\/$/, '')}/${url.replace(/^\//, '')}`;
    const reqKey = generateReqKey(fullUrl, method, data);
    
    if (cancelTokenMap.has(reqKey)) {
      const cancel = cancelTokenMap.get(reqKey);
      cancel('请求已取消（页面销毁或主动触发）');
      cancelTokenMap.delete(reqKey);
    }
  } else {
    cancelTokenMap.forEach((cancel) => {
      cancel('所有请求已取消（页面销毁）');
    });
    cancelTokenMap.clear();
  }
};

/**
 * 核心请求函数
 */
const request = (options = {}) => {
  // 2. 默认配置（直接使用 uni.getSystemInfoSync() 全局方法）
  const systemInfo = uni.getSystemInfoSync(); // 获取系统信息（替代导入的 getSystemInfoSync）
  const defaultOptions = {
    url: '',
    method: 'GET',
    data: {},
    header: {
      'Content-Type': 'application/json;charset=UTF-8',
      'X-Platform': systemInfo.platform, // 使用全局 uni 方法的结果
      'X-App-Version': systemInfo.appVersion || '1.0.0'
    },
    timeout: 15000,
    withToken: true,
    showLoading: true,
    loadingText: '加载中...',
    baseURL: process.env.NODE_ENV === 'development' 
      ? 'https://dev-api.xxx.com' 
      : 'https://prod-api.xxx.com'
  };

  const finalOptions = { ...defaultOptions, ...options };
  finalOptions.url = `${finalOptions.baseURL.replace(/\/$/, '')}/${finalOptions.url.replace(/^\//, '')}`;
  finalOptions.success = undefined;
  finalOptions.fail = undefined;
  finalOptions.complete = undefined;

  // 3. 请求拦截器逻辑
  const reqKey = generateReqKey(finalOptions.url, finalOptions.method, finalOptions.data);
  
  if (finalOptions.showLoading) {
    // 直接使用 uni.showLoading() 全局方法（替代导入的 showLoading）
    uni.showLoading({
      title: finalOptions.loadingText,
      mask: true
    });
  }

  if (finalOptions.withToken) {
    // 直接使用 uni.getStorageSync() 全局方法（替代导入的 getStorageSync）
    const token = uni.getStorageSync('token');
    if (token) {
      finalOptions.header.Authorization = `Bearer ${token}`;
    }
  }

  const commonParams = {
    deviceId: uni.getStorageSync('deviceId') || systemInfo.deviceId,
    timestamp: Date.now()
  };

  if (finalOptions.method.toUpperCase() === 'GET') {
    finalOptions.data = { ...commonParams, ...finalOptions.data };
  } else {
    finalOptions.data = { ...finalOptions.data, ...commonParams };
  }

  // 4. 发起请求
  return new Promise((resolve, reject) => {
    const cancel = (reason) => {
      if (requestTask) {
        requestTask.abort();
        reject(new Error(`请求取消：${reason}`));
      }
      if (finalOptions.showLoading) {
        uni.hideLoading(); // 直接使用全局方法
      }
    };
    cancelTokenMap.set(reqKey, cancel);

    const requestTask = uni.request({
      ...finalOptions,
      success: (res) => {
        cancelTokenMap.delete(reqKey);
        if (finalOptions.showLoading) uni.hideLoading();

        const { statusCode, data: resData } = res;
        if (statusCode < 200 || statusCode >= 300) {
          const errMsg = `网络错误：HTTP ${statusCode}`;
          // 直接使用 uni.showToast() 全局方法（替代导入的 showToast）
          uni.showToast({ title: errMsg, icon: 'none', duration: 2500 });
          reject(new Error(errMsg));
          return;
        }

        const { code, data: businessData, message = '操作失败' } = resData;
        switch (code) {
          case 200:
            resolve(businessData);
            break;
          case 401:
            // 直接使用 uni.removeStorageSync() 全局方法
            uni.removeStorageSync('token');
            // 直接使用 uni.showModal() 全局方法
            uni.showModal({
              title: '登录过期',
              content: '您的登录已失效，请重新登录',
              showCancel: false,
              success: () => {
                uni.reLaunch({ url: '/pages/login/login' });
              }
            });
            reject(new Error(`未登录：${message}`));
            break;
          case 403:
            uni.showToast({ title: message || '权限不足', icon: 'none', duration: 2500 });
            reject(new Error(`权限不足：${message}`));
            break;
          case 500:
            uni.showModal({
              title: '服务器繁忙',
              content: message || '请稍后重试',
              cancelText: '取消',
              confirmText: '重试',
              success: (modalRes) => {
                if (modalRes.confirm) {
                  request(finalOptions).then(resolve).catch(reject);
                }
              }
            });
            reject(new Error(`服务器错误：${message}`));
            break;
          default:
            uni.showToast({ title: message, icon: 'none', duration: 2500 });
            reject(new Error(`业务错误[${code}]：${message}`));
            break;
        }
      },
      fail: (err) => {
        cancelTokenMap.delete(reqKey);
        if (finalOptions.showLoading) uni.hideLoading();

        let errMsg = '请求失败，请检查网络';
        if (err.errMsg.includes('timeout')) {
          errMsg = '请求超时，请稍后重试';
        } else if (err.errMsg.includes('abort')) {
          errMsg = '请求已取消';
        }

        uni.showToast({ title: errMsg, icon: 'none', duration: 2500 });
        reject(new Error(errMsg));
      }
    });
  });
};

// 5. 封装请求方法
const http = {
  baseURL: process.env.NODE_ENV === 'development' 
    ? 'https://dev-api.xxx.com' 
    : 'https://prod-api.xxx.com',

  get(url, data = {}, options = {}) {
    return request({ ...options, url, method: 'GET', data });
  },

  post(url, data = {}, options = {}) {
    return request({ ...options, url, method: 'POST', data });
  },

  put(url, data = {}, options = {}) {
    return request({ ...options, url, method: 'PUT', data });
  },

  delete(url, data = {}, options = {}) {
    return request({ ...options, url, method: 'DELETE', data });
  },

  cancelRequest: cancelRequest
};

export default http;