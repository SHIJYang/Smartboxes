// utils/WechatPay.js

/**
 * 微信支付通用工具类（支持多端）
 * @param {Object} options
 * @param {string} options.order_number - 订单号
 * @param {Function} options.onSuccess - 支付成功回调
 * @param {Function} options.onCancel - 支付取消回调
 * @param {Function} options.onError - 支付失败回调
 * @param {Function} options.buildOrderInfo - 获取支付参数的 API 函数（返回 Promise）
 * @param {Function} options.buildQrCodeUrl - 获取支付二维码 URL 的 API 函数（H5/PC 用）
 */
export default class WechatPay {
    constructor(options) {
        this.order_number = options.order_number;
        this.onSuccess = options.onSuccess || (() => {});
        this.onCancel = options.onCancel || (() => {});
        this.onError = options.onError || (() => {});
        this.buildOrderInfo = options.buildOrderInfo;      // 用于小程序/App/H5-JSAPI
        this.buildQrCodeUrl = options.buildQrCodeUrl;      // 用于 PC/H5非微信
    }

    // 获取当前平台
    _getPlatform() {
        if (typeof uni !== 'undefined' && typeof uni.getEnv === 'function') {
            const env = uni.getEnv();
            if (env === 'MP-WEIXIN') return 'MP';
            if (env === 'APP-PLUS') return 'APP';
            if (env === 'H5') return 'H5';
        }

        // H5 环境：判断是否在微信中
        const ua = typeof navigator !== 'undefined' ? navigator.userAgent : '';
        if (/micromessenger/i.test(ua)) {
            return 'H5_IN_WECHAT';
        }

        // 默认视为 PC 或普通 H5
        return 'PC';
    }

    // 主入口：开始支付
    async pay() {
        const platform = this._getPlatform();
        console.log('[WechatPay] 当前平台:', platform);

        try {
            if (platform === 'MP' || platform === 'APP') {
                await this._payInMiniOrApp();
            } else if (platform === 'H5_IN_WECHAT') {
                await this._payInWechatH5();
            } else {
                // PC 或 非微信 H5 → 显示二维码
                await this._showQrCode();
            }
        } catch (err) {
            console.error('[WechatPay] 支付异常:', err);
            this.onError(err.message || '支付初始化失败');
        }
    }

    // 小程序 / App 支付
    async _payInMiniOrApp() {
        const payData = await this.buildOrderInfo(this.order_number);
        return new Promise((resolve, reject) => {
            uni.requestPayment({
                provider: 'wxpay',
                orderInfo: payData,
                success: () => {
                    this.onSuccess();
                    resolve();
                },
                fail: (err) => {
                    if (err.errMsg && err.errMsg.includes('cancel')) {
                        this.onCancel();
                    } else {
                        this.onError('支付失败');
                    }
                    reject(err);
                }
            });
        });
    }

    // 微信内 H5 支付（JSAPI）
    async _payInWechatH5() {
        const payData = await this.buildOrderInfo(this.order_number);

        return new Promise((resolve, reject) => {
            const invokePay = () => {
                WeixinJSBridge.invoke(
                    'getBrandWCPayRequest',
                    {
                        appId: payData.appId,
                        timeStamp: String(payData.timeStamp), // 必须字符串
                        nonceStr: payData.nonceStr,
                        package: payData.package, // prepay_id=xxx
                        signType: payData.signType || 'MD5',
                        paySign: payData.paySign
                    },
                    (res) => {
                        if (res.err_msg === 'get_brand_wcpay_request:ok') {
                            this.onSuccess();
                            resolve();
                        } else if (res.err_msg === 'get_brand_wcpay_request:cancel') {
                            this.onCancel();
                            reject(new Error('用户取消'));
                        } else {
                            this.onError('支付失败');
                            reject(new Error('支付失败'));
                        }
                    }
                );
            };

            if (typeof WeixinJSBridge === 'undefined') {
                document.addEventListener('WeixinJSBridgeReady', invokePay, false);
            } else {
                invokePay();
            }
        });
    }

    // PC / 非微信 H5：显示二维码
    async _showQrCode() {
        const qrCodeUrl = await this.buildQrCodeUrl(this.order_number);
        this.onShowQrCode(qrCodeUrl); // 由外部实现 UI 展示
    }

    // 外部需实现：展示二维码（例如弹出 modal）
    onShowQrCode(qrCodeUrl) {
        console.warn('[WechatPay] 请在调用处实现 onShowQrCode 方法，展示二维码:', qrCodeUrl);
        // 示例：uni.showModal + image，或跳转到扫码页
    }
}