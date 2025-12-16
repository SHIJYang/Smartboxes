<template>
	<view class="form">
		<view class="item"><text>物品名称</text><input v-model="form.itemName" /></view>
		<view class="item"><text>分类标签</text><input v-model="form.itemTag" /></view>
		<view class="item"><text>价格</text><input type="number" v-model.number="form.price" /></view>
		<button class="save-btn" @click="submit">保存物品</button>
	</view>
</template>
<script setup lang="ts">
	import { ref, onMounted } from 'vue';
	import { onShow, onLoad } from '@dcloudio/uni-app';
	import { saveItem, getItemDetail } from '@/api/index';
	import type { ItemDTO } from '@/common/types';
	const form = ref<ItemDTO>({ id: 0, boxId: 0, itemName: '', price: 0 });
	onLoad(async (o : any) => {
		if (o.boxId) form.value.boxId = parseInt(o.boxId);
		if (o.id) {
			const res = await getItemDetail(parseInt(o.id));
			if (res.code === 200) form.value = res.data;
		}
	});
	const submit = async () => { await saveItem(form.value); uni.navigateBack(); }
</script>
<style>
	.form {
		padding: 20px
	}

	.item {
		margin-bottom: 15px;
		border-bottom: 1px solid #eee
	}

	.save-btn {
		background: #007aff;
		color: #fff
	}
</style>