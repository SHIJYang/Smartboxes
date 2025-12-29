{
type: uploaded file
fileName: itemedit.vue
fullContent:
<template>
  <view class="form-page">
    <PCHeader current="item" />
    <view class="pc-placeholder"></view>
    <view class="form-body">
      <view class="form-group">
        <text class="label">📦 所属盒子</text>
        <picker mode="selector" :range="boxOptions" :value="boxIndex" @change="onBoxChange">
          <view class="picker-value">{{ boxOptions[boxIndex] || '点击选择盒子' }}</view>
        </picker>
      </view>
      <view class="form-group">
        <text class="label">🔢 物品编码 *</text>
        <input v-model="form.itemCode" class="input" placeholder="唯一标识，如 SN2025XXXX" />
      </view>
      <view class="form-group">
        <text class="label">✏️ 自定义名称</text>
        <input v-model="form.manualEditName" class="input" placeholder="留空则使用识别名" />
      </view>
      <view class="form-group">
        <text class="label">🏷️ 标签</text>
        <input v-model="form.itemTag" class="input" placeholder="如 衣物/药品/工具" />
      </view>
      <view class="form-group">
        <text class="label">📝 描述</text>
        <textarea v-model="form.itemDesc" class="textarea" placeholder="可选，记录细节" />
      </view>
      
      <view class="form-group">
        <text class="label">📅 存入时间</text>
        <picker mode="date" :value="putInDate" @change="onPutInDateChange">
          <view class="picker-value">{{ putInDate || '请选择日期' }}</view>
        </picker>
      </view>
      <view class="form-group">
        <text class="label">⏳ 过期时间</text>
        <picker mode="date" :value="expireDate" @change="onExpireDateChange">
          <view class="picker-value">{{ expireDate || '无' }}</view>
        </picker>
      </view>
    </view>
    
    <view class="action-bar">
      <view v-if="form.id" class="delete-btn" @click="remove">🗑️ 删除</view>
      <button class="submit-btn" :loading="submitting" @click="submit">保存物品</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useItemStore, useBoxStore } from '@/stores';
import type { ItemDTO } from '@/common/types';
import PCHeader from '@/components/PCHeader.vue';

const itemStore = useItemStore();
const boxStore = useBoxStore();

const form = ref<ItemDTO>({
  id: undefined,
  boxId: 1,
  itemCode: '',
  manualEditName: '',
  autoRecognizeName: '',
  itemTag: '',
  itemDesc: '',
  putInTime: undefined,
  expireTime: undefined,
  isValid: 1
});

const submitting = ref(false);
const boxOptions = ref<string[]>([]);
const boxIndex = ref(0);
const putInDate = ref('');
const expireDate = ref('');

// 初始化盒子选项
const initBoxes = async () => {
  await boxStore.fetchBoxList({ userId: 1001 });
  const boxes = boxStore.boxList;
  boxOptions.value = boxes.map(b => `${b.boxName} (${b.boxCode})`);
  
  // Set initial index based on form.boxId
  if (form.value.boxId) {
    const idx = boxes.findIndex(b => b.id === form.value.boxId);
    if (idx >= 0) boxIndex.value = idx;
  }
};

onLoad(async (opt: Record<string, any>) => {
  await initBoxes();

  if (opt.boxId) {
    form.value.boxId = parseInt(opt.boxId);
    // Re-calculate index
    const idx = boxStore.boxList.findIndex(b => b.id === form.value.boxId);
    if (idx >= 0) boxIndex.value = idx;
  }

  if (opt.id) {
    uni.setNavigationBarTitle({ title: '编辑物品' });
    const id = parseInt(opt.id);
    await itemStore.fetchItemDetail(id);
    
    // Get from store state which is now populated
    const data = itemStore.currentItem;
    if (data && data.id === id) {
      form.value = { ...data };
      if (data.putInTime) putInDate.value = new Date(data.putInTime).toISOString().split('T')[0];
      if (data.expireTime) expireDate.value = new Date(data.expireTime).toISOString().split('T')[0];
    }
  } else {
    uni.setNavigationBarTitle({ title: '添加物品' });
    // Default putInTime to now
    putInDate.value = new Date().toISOString().split('T')[0];
    form.value.putInTime = new Date().toISOString();
  }
});

const onBoxChange = (e: any) => {
  const idx = e.detail.value;
  boxIndex.value = idx;
  form.value.boxId = boxStore.boxList[idx]?.id || 1;
};

const onPutInDateChange = (e: any) => {
  putInDate.value = e.detail.value;
  form.value.putInTime = new Date(e.detail.value).toISOString(); // Using ISO string for API
};

const onExpireDateChange = (e: any) => {
  expireDate.value = e.detail.value;
  form.value.expireTime = new Date(e.detail.value).toISOString();
};

const submit = async () => {
  if (!form.value.itemCode?.trim()) {
    return uni.showToast({ title: '物品编码是必填的哦', icon: 'none' });
  }
  if (!form.value.boxId) {
    return uni.showToast({ title: '请指定一个盒子', icon: 'none' });
  }

  submitting.value = true;
  try {
    const result = await itemStore.addItem(form.value); // Use addItem/saveItem wrapper if available
    // OR if store has separate update/add, check id
    // const result = form.value.id ? await itemStore.updateItem(form.value) : await itemStore.addItem(form.value);
    
    if (result.success) {
      uni.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 800);
    } else {
      uni.showToast({ title: result.message || '保存失败', icon: 'none' });
    }
  } finally {
    submitting.value = false;
  }
};

const remove = async () => {
  uni.showModal({
    title: '删除确认',
    content: '确定要丢弃这个物品记录吗？',
    confirmColor: '#FF9A9E',
    success: async (res) => {
      if (res.confirm && form.value.id) {
        const result = await itemStore.deleteItem(form.value.id);
        if (result.success) {
          uni.navigateBack();
        }
      }
    }
  });
};
</script>

<style lang="scss" scoped>
$bg-color: #FFF9F0;
$primary-gradient: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);

.form-page {
  min-height: 100vh;
  background-color: $bg-color;
  padding: 40rpx;
  position: relative;
}
.pc-placeholder { display: none; height: 80px; @media screen and (min-width: 768px) { display: block; } }

.form-body {
  background: #fff;
  border-radius: 40rpx;
  padding: 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(161, 140, 209, 0.1);
  margin-top: 20rpx;
}

.form-group {
  margin-bottom: 40rpx;
  &:last-child { margin-bottom: 0; }
  
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: bold; padding-left: 10rpx; }
  
  .input, .picker-value, .textarea {
    background: #F8F8F8;
    border-radius: 24rpx;
    padding: 20rpx 30rpx;
    font-size: 30rpx; color: #333;
    border: 2rpx solid transparent;
    transition: all 0.3s;
    min-height: 80rpx;
    display: flex; align-items: center;
    &:focus { background: #fff; border-color: #a18cd1; }
  }
  .textarea { height: 150rpx; align-items: flex-start; }
}

.action-bar {
  margin-top: 60rpx;
  .submit-btn {
    background: $primary-gradient;
    color: #fff; border-radius: 50rpx;
    font-size: 34rpx; font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(161, 140, 209, 0.3);
    margin-bottom: 30rpx;
    &::after { border: none; }
  }
  .delete-btn {
    text-align: center; color: #ff6b81; font-size: 28rpx; margin-bottom: 20rpx; opacity: 0.8;
  }
}
</style>
}