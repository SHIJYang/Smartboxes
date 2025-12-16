<template>
  <view class="form">
    <view class="item">
      <text>名称</text>
      <input v-model="form.boxName" placeholder="如：客厅主箱" />
    </view>
    <view class="item">
      <text>编号</text>
      <input v-model="form.boxCode" placeholder="输入设备编码" />
    </view>
    <button class="save-btn" @click="submit">保存</button>
    <button v-if="form.id" class="del-btn" @click="remove">删除盒子</button>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
	import { onShow, onLoad } from '@dcloudio/uni-app';
import { getBoxDetail, saveBox, deleteBox } from '@/api/index';
import type { BoxDTO } from '@/common/types';

const form = ref<BoxDTO>({ id: 0, boxName: '', boxCode: '', userId: 1001, status: 0, boxType: 1 });

onLoad(async (opt: any) => {
  if (opt.id) {
    const res = await getBoxDetail(parseInt(opt.id));
    if (res.code === 200) form.value = res.data;
  }
});

const submit = async () => {
  await saveBox(form.value);
  uni.showToast({ title: '保存成功' });
  setTimeout(() => uni.navigateBack(), 800);
};

const remove = async () => {
  await deleteBox(form.value.id);
  uni.navigateBack();
};
</script>

<style>
.form { padding: 20px; }
.item { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
.item text { display: block; margin-bottom: 5px; font-size: 14px; color: #666; }
.save-btn { background: #007aff; color: #fff; margin-top: 20px; }
.del-btn { background: #fff; color: red; margin-top: 10px; border: 1px solid red; }
</style>