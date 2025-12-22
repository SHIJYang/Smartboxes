<template>
	<view class="pc-nav-wrapper">
		<view class="pc-nav-content">
			<view class="logo" @click="go('/pages/index/index')">
				<text class="emoji">✨</text>
				<text class="title">MagicBox</text>
			</view>

			<view class="menu">
				<view class="menu-item" :class="{active: current === 'index'}" @click="go('/pages/index/index')">大厅</view>
				<view class="menu-item" :class="{active: current === 'box'}" @click="go('/pages/box/boxlist')">盒子</view>
				<view class="menu-item" :class="{active: current === 'chat'}" @click="go('/pages/chat/chat')">助手</view>
				<view class="menu-item" :class="{active: current === 'user'}" @click="go('/pages/user/user')">我的</view>
			</view>

			<view class="actions">
				<view class="search-pill" @click="go('/pages/item/itemlist')">
					<text>🔍</text>
				</view>
				<view class="avatar-circle" @click="go('/pages/user/user')"></view>
			</view>
		</view>
	</view>
</template>

<script setup>
import { defineProps } from 'vue';

const props = defineProps({
	current: { type: String, default: 'index' }
});

const go = (url) => {
	uni.switchTab({ url, fail: () => uni.navigateTo({ url }) });
};
</script>

<style scoped lang="scss">
.pc-nav-wrapper {
	display: none; /* 手机端隐藏 */
	position: fixed;
	top: 0; left: 0; right: 0;
	height: 70px;
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(20px);
	box-shadow: 0 4px 20px rgba(255, 154, 158, 0.15);
	z-index: 999;

	@media screen and (min-width: 768px) {
		display: block;
	}
}

.pc-nav-content {
	max-width: 1200px;
	margin: 0 auto;
	height: 100%;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 20px;
}

.logo {
	display: flex; align-items: center; cursor: pointer;
	.emoji { font-size: 28px; margin-right: 8px; }
	.title { font-size: 22px; font-weight: 800; color: #333; letter-spacing: -0.5px; }
}

.menu {
	display: flex; gap: 40px;
	.menu-item {
		font-size: 16px; font-weight: bold; color: #888; cursor: pointer; position: relative;
		&.active { color: #FF9A9E; }
		&.active::after {
			content: ''; position: absolute; bottom: -5px; left: 50%; transform: translateX(-50%);
			width: 6px; height: 6px; background: #FF9A9E; border-radius: 50%;
		}
		&:hover { color: #FF9A9E; }
	}
}

.actions {
	display: flex; align-items: center; gap: 20px;
	.search-pill {
		background: #FFF0F5; color: #aaa; padding: 8px 16px; border-radius: 20px; font-size: 14px;
	}
	.avatar-circle {
		width: 36px; height: 36px; background: linear-gradient(135deg, #a18cd1, #fbc2eb); border-radius: 50%; cursor: pointer;
	}
}
</style>