<script>
	export default {
		onLaunch: function() {
			this.checkDevice();
		},
		onShow: function() {
			this.checkDevice();
			// #ifdef H5
			window.addEventListener('resize', this.checkDevice);
			// #endif
		},
		methods: {
			checkDevice() {
				const systemInfo = uni.getSystemInfoSync();
				// 电脑端隐藏底部 TabBar，显示顶部 PCHeader
				if (systemInfo.windowWidth > 768) {
					uni.hideTabBar();
				} else {
					uni.showTabBar();
				}
			}
		}
	}
</script>

<style lang="scss">
	
	:root {
		--bg-color: #FFF9F0;          /* 奶油白背景 */
		--card-bg: #FFFFFF;           /* 卡片纯白 */
		--primary-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%); /* 糖果粉渐变 */
		--secondary-gradient: linear-gradient(120deg, #a18cd1 0%, #fbc2eb 100%); /* 香芋紫渐变 */
		--text-main: #333333;
		--text-sub: #999999;
		--shadow-soft: 0 8rpx 30rpx rgba(255, 154, 158, 0.2); /* 柔和粉色阴影 */
		--radius-box: 30rpx;
	}

	page {
		background-color: var(--bg-color);
		font-family: 'Nunito', 'Segoe UI', system-ui, sans-serif;
		color: var(--text-main);
	}

	/* 通用：隐藏滚动条 */
	::-webkit-scrollbar {
		width: 0;
		height: 0;
		color: transparent;
	}
	
	/* 通用：PC端 顶部占位符 (防止导航栏挡住内容) */
	.pc-placeholder {
		display: none;
		height: 80px;
		@media screen and (min-width: 768px) {
			display: block;
		}
	}
</style>