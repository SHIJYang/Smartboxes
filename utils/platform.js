// 平台判断工具类
export const platform = {
    isH5: process.env.VUE_APP_PLATFORM === 'h5',
    isApp: process.env.VUE_APP_PLATFORM === 'app-plus',
    isWeapp: process.env.VUE_APP_PLATFORM === 'mp-weixin',
    // 获取导航栏高度
    getNavHeight() {
        if (this.isWeapp) {
            const systemInfo = uni.getSystemInfoSync()
            return systemInfo.statusBarHeight + 44
        }
        return this.isH5 ? 44 : 0
    }
}

// 平台特定样式
export const platformStyle = {
    // 安全区域：使用 platform 对象而非 this
    getSafeAreaBottom() {
        return platform.isWeapp ? 'padding-bottom: env(safe-area-inset-bottom);' : ''
    }
}
