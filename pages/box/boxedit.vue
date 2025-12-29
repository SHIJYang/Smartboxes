{
type: uploaded file
fileName: boxedit.vue
fullContent:
<template>
  <view class="form-page">
    <PCHeader current="box" />
    <view class="pc-placeholder"></view>
    <view class="form-body">
      <view class="form-group">
        <text class="label">📦 盒子名称 *</text>
        <input v-model="form.boxName" class="input" placeholder="如：冬季衣物箱" />
      </view>
      <view class="form-group">
        <text class="label">🔢 盒子编码 *</text>
        <input v-model="form.boxCode" class="input" placeholder="唯一标识，如 BOX2025XXXX" />
      </view>
      <view class="form-group">
        <text class="label">⚙️ 状态</text>
        <switch :checked="form.status === 1" @change="onStatusChange" color="#FF9A9E" />
        <text class="switch-label">{{ form.status === 1 ? '启用' : '停用' }}</text>
      </view>
      <view class="form-group">
        <text class="label">❄️ 类型</text>
        <radio-group @change="onTypeChange" class="radio-grp">
          <label class="radio-label"><radio :value="'1'" :checked="form.boxType === 1" color="#FF9A9E" /> 普通箱</label>
          <label class="radio-label"><radio :value="'2'" :checked="form.boxType === 2" color="#FF9A9E" /> 冷藏箱</label>
        </radio-group>
      </view>
    </view>
    <view class="action-bar">
      <view v-if="form.id" class="delete-btn" @click="remove">🗑️ 删除</view>
      <button class="submit-btn" :loading="submitting" @click="submit">保存盒子</button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useBoxStore } from '@/stores/boxStore';
import type { BoxDTO } from '@/common/types';
import PCHeader from '@/components/PCHeader.vue';

const store = useBoxStore();

const form = ref<BoxDTO>({
  id: undefined,
  boxName: '',
  boxCode: '',
  userId: 1001,
  status: 0,
  boxType: 1
});

const submitting = ref(false);

onLoad(async (opt: Record<string, any>) => {
  if (opt.id) {
    uni.setNavigationBarTitle({ title: '编辑盒子' });
    const id = parseInt(opt.id);
    await store.fetchBoxDetail(id);
    if (store.currentBox) {
      // Shallow copy to break reference
      form.value = { ...store.currentBox };
    }
  } else {
    uni.setNavigationBarTitle({ title: '新建盒子' });
  }
});

const onStatusChange = (e: any) => {
  form.value.status = e.detail.value ? 1 : 0;
};

const onTypeChange = (e: any) => {
  form.value.boxType = parseInt(e.detail.value);
};

const submit = async () => {
  if (!form.value.boxName?.trim() || !form.value.boxCode?.trim()) {
    return uni.showToast({ title: '盒子名称和编码不能为空', icon: 'none' });
  }

  submitting.value = true;
  try {
    const result = await store.saveBox(form.value);
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
    title: '确认删除',
    content: '删除后无法恢复，确定吗？',
    confirmColor: '#FF9A9E',
    success: async (res) => {
      if (res.confirm && form.value.id) {
        const result = await store.deleteBox(form.value.id);
        if (result.success) uni.navigateBack();
      }
    }
  });
};
</script>

<style lang="scss" scoped>
$bg-color: #FFF9F0;
$primary-gradient: linear-gradient(135deg, #FF9A9E 0%, #FECFEF 100%);

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
  .label { font-size: 28rpx; color: #666; margin-bottom: 16rpx; font-weight: bold; display: block; padding-left: 10rpx;}
  
  .input {
    background: #F8F8F8;
    border-radius: 24rpx;
    padding: 20rpx 30rpx;
    height: 80rpx;
    font-size: 30rpx; color: #333;
    border: 2rpx solid transparent;
    transition: all 0.3s;
    &:focus { background: #fff; border-color: #FF9A9E; }
  }
  
  .switch-label { margin-left: 20rpx; color: #555; font-size: 28rpx; }
  
  .radio-grp { display: flex; gap: 40rpx; }
  .radio-label { display: flex; align-items: center; font-size: 28rpx; color: #555; }
}

.action-bar {
  margin-top: 60rpx;
  .submit-btn {
    background: $primary-gradient;
    color: #fff; border-radius: 50rpx;
    font-size: 34rpx; font-weight: bold;
    box-shadow: 0 10rpx 20rpx rgba(255, 154, 158, 0.3);
    margin-bottom: 30rpx;
    &::after { border: none; }
  }
  .delete-btn {
    text-align: center; color: #ff6b81; font-size: 28rpx; margin-bottom: 20rpx;
    opacity: 0.8;
  }
}
</style>
}