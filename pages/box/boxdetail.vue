<template>
	<view class="container" v-if="box">
		<view class="header">
			<text class="h1">{{ box.boxName }}</text>
			<text class="btn-text" @click="goEdit">编辑信息</text>
		</view>
		<view class="info">
			<text>状态: {{ box.status===1?'在线':'离线' }} | 电量: {{ box.battery }}%</text>
		</view>
		<view class="section-title">盒内物品</view>
		<view v-for="item in items" :key="item.id" class="item-row" @click="goItem(item.id)">
			<text>{{ item.itemName }}</text>
			<text class="price">¥{{ item.price }}</text>
		</view>
		<button class="plain-btn" @click="addItem">+ 添加物品</button>
	</view>
</template>
<script setup lang="ts">
	import { ref, onMounted } from 'vue';
	import { onShow, onLoad } from '@dcloudio/uni-app';
	import { getBoxDetail, getItemList } from '@/api/index';
	import type { BoxDTO, ItemDTO } from '@/common/types';

	const box = ref<BoxDTO>();
	const items = ref<ItemDTO[]>([]);
	const boxId = ref(0);

	onLoad((opt : any) => {
		boxId.value = parseInt(opt.id);
		loadData();
	});

	const loadData = async () => {
		const bRes = await getBoxDetail(boxId.value);
		if (bRes.code === 200) box.value = bRes.data;

		const iRes = await getItemList({ boxId: boxId.value });
		if (iRes.code === 200) items.value = iRes.data; // Mock模式下返回所有Mock数据
	};

	const goEdit = () => uni.navigateTo({ url: `/pages/box/boxedit?id=${boxId.value}` });
	const addItem = () => uni.navigateTo({ url: `/pages/item/itemadd?boxId=${boxId.value}` });
	const goItem = (id : number) => uni.navigateTo({ url: `/pages/item/itemedit?id=${id}` });
</script>
<style>
	.container {
		padding: 20px;
	}

	.header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 10px;
	}

	.h1 {
		font-size: 22px;
		font-weight: bold;
	}

	.btn-text {
		color: #007aff;
	}

	.info {
		color: #666;
		font-size: 14px;
		margin-bottom: 30px;
	}

	.section-title {
		font-weight: bold;
		margin-bottom: 10px;
		border-left: 4px solid #007aff;
		padding-left: 10px;
	}

	.item-row {
		display: flex;
		justify-content: space-between;
		padding: 15px 0;
		border-bottom: 1px solid #eee;
	}

	.price {
		color: #ff9800;
	}

	.plain-btn {
		margin-top: 20px;
		border: 1px dashed #999;
		background: none;
		color: #666;
	}
</style>