<template>
	<view>
		
	</view>
</template>

<script setup>
	// pages/order-detail/order-detail.vue
	import WechatPay from '@/utils/WechatPay.js';
	
	export default {
	    methods: {
	        async handleWechatPay() {
	            const pay = new WechatPay({
	                order_number: this.products.order_number,
	
	                // 获取支付参数（小程序/App/H5-JSAPI）
	                buildOrderInfo: (order_number) => {
	                    return this.$request.register('wx_pay/buildOrderInfo', { order_number });
	                },
	
	                // 获取二维码 URL（PC/H5非微信）
	                buildQrCodeUrl: (order_number) => {
	                    return this.$request.register('wx_pay/buildQrCodeUrl', { order_number });
	                },
	
	                onSuccess: () => {
	                    uni.showToast({ title: '支付成功' });
	                    this.products.status = 1;
	                    clearInterval(timer);
	                },
	                onCancel: () => {
	                    uni.showToast({ title: '已取消支付', icon: 'none' });
	                },
	                onError: (msg) => {
	                    uni.showToast({ title: msg, icon: 'none' });
	                },
	
	                // 自定义二维码展示方式
	                onShowQrCode: (qrCodeUrl) => {
	                    uni.navigateTo({
	                        url: `/pages/pay/qr-code?qrUrl=${encodeURIComponent(qrCodeUrl)}`
	                    });
	                }
	            });
	
	            await pay.pay();
	        }
	    }
	}
</script>

<style>
	       
</style>
