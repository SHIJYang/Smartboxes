<template>
	<view class="container">
		<u-navbar title="我的收纳空间" title-bold title-color="#333" />

		<view class="body">
			<u-empty v-if="!loading && spaceList.length === 0" text="暂无收纳空间" mode="data" class="empty-state">
				<u-button type="primary" size="mini" @click="addSpace">立即创建</u-button>
			</u-empty>

			<view v-else-if="loading" class="loading-wrap">
				<u-loading mode="circle" size="50" color="#409eff" text="加载中..." />
			</view>

			<scroll-view v-else scroll-y enable-flex class="space-list">
				<view class="grid">
					<boxcard v-for="space in spaceList" :key="space.id" :name="space.name || space.boxName || '未命名收纳盒'"
						:category="space.category || 'box'" :item-count="space.itemCount || getRandomItemCount()"
						:location="space.location || getRandomLocation()"
						:last-modified="space.lastModified || space.lastHeartbeatTime"
						:battery-level="space.batteryLevel || space.battery || 0"
						:is-charging="space.isCharging || false" @click="gotoSpace(space.id)" class="card" />
				</view>
			</scroll-view>
		</view>

		<u-fab icon="plus" position="bottomRight" :offset="[32, 88]" @click="addSpace" />
	</view>
</template>

<script setup>
	import {
		ref,
		onMounted
	} from "vue";

	import http from "@/utils/request";
	import {
		getBoxes
	} from "@/api/boxesapi.js";
	import boxcard from "@/components/boxcard.vue";

	const spaceList = ref([]);
	const loading = ref(false);

	onMounted(() => {
		loadData();
	});

	const loadData = async () => {
		loading.value = true;
		try {
			const res = await getBoxes();
			console.log("666")
			spaceList.value = Array.isArray(res) ? res : [];
		} catch (err) {
			uni.showToast({
				title: "加载失败",
				icon: "none"
			});
			spaceList.value = [];
		} finally {
			loading.value = false;
		}
	};

	const gotoSpace = (id) => {
		if (!id) return;
		uni.navigateTo({
			url: `/pages/space/space?id=${id}`
		});
	};

	const addSpace = () => {
		uni.showToast({
			title: "功能开发中",
			icon: "none"
		});
	};
</script>

<style scoped>
	.container {
		min-height: 100vh;
		background-color: #f8f9fa;
		padding-bottom: env(safe-area-inset-bottom, 16px);
		display: flex;
		flex-direction: column;
	}

	.body {
		flex: 1;
		padding: 20rpx;
		box-sizing: border-box;
	}

	.loading-wrap {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 60vh;
	}

	.empty-state {
		display: flex;
		align-items: center;
		justify-content: center;
		height: 60vh;
		flex-direction: column;
		gap: 12rpx;
	}

	.space-list {
		flex: 1;
	}

	.grid {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
		padding-bottom: 20rpx;
	}

	.card {
		width: calc(50% - 12rpx);
		box-sizing: border-box;
	}
</style>