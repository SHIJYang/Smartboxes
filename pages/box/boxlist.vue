<template>
	<view class="container">
		<view v-for="box in list" :key="box.id" class="card" @click="goDetail(box.id)">
			<view class="row">
				<text class="title">{{ box.boxName }}</text>
				<text :class="['status', box.status===1?'on':'off']">{{ box.status===1?'在线':'离线' }}</text>
			</view>
			<text class="code">编号: {{ box.boxCode }}</text>
		</view>
		<button class="add-btn" @click="goEdit()">+ 新建盒子</button>
	</view>
</template>
<script setup lang="ts">
	import { ref, onMounted } from 'vue';
	import { onShow, onLoad } from '@dcloudio/uni-app';
	import { getBoxList } from '@/api/index';
	import type { BoxDTO } from '@/common/types';

	const list = ref<BoxDTO[]>([]);

	// 使用 onShow 确保每次返回刷新
	onShow(() => loadData());

	const loadData = async () => {
		const res = await getBoxList(1001);
		if (res.code === 200) list.value = res.data;
	};

	const goDetail = (id : number) => uni.navigateTo({ url: `/pages/box/boxdetail?id=${id}` });
	const goEdit = () => uni.navigateTo({ url: '/pages/box/boxedit' });
</script>
<style>
	.container {
		padding: 15px;
		padding-bottom: 80px;
		background-color: #f5f5f5;
		min-height: 100vh;
	}

	.card {
		background: #fff;
		padding: 15px;
		border-radius: 8px;
		margin-bottom: 10px;
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
	}

	.row {
		display: flex;
		justify-content: space-between;
		margin-bottom: 5px;
		align-items: center;
	}

	.title {
		font-size: 16px;
		font-weight: bold;
		color: #333;
	}

	.status {
		font-size: 12px;
		padding: 3px 8px;
		border-radius: 12px;
		font-weight: 500;
	}

	.status.on {
		background: #e1f3d8;
		color: #67c23a;
	}

	.status.off {
		background: #f4f4f5;
		color: #909399;
	}

	.code {
		color: #999;
		font-size: 13px;
	}

	.add-btn {
		position: fixed;
		bottom: 80px;
		left: 50%;
		transform: translateX(-50%);
		width: 80%;
		max-width: 300px;
		background: #007aff;
		color: #fff;
		border-radius: 25px;
		height: 50px;
		font-size: 16px;
		font-weight: 500;
	}
</style>