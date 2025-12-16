<template>
  <view class="container">
    <view class="form-group">
      <view class="form-item">
        <text class="label">物品名称 <text class="required">*</text></text>
        <input class="input" v-model="formData.itemName" placeholder="请输入名称" />
      </view>
      
      <view class="form-item">
        <text class="label">所属盒子ID <text class="required">*</text></text>
        <input class="input" type="number" v-model.number="formData.boxId" placeholder="请输入盒子ID" />
      </view>

      <view class="form-item">
        <text class="label">价格</text>
        <input class="input" type="digit" v-model.number="formData.price" placeholder="0.00" />
      </view>
      
      <view class="form-item">
        <text class="label">标签</text>
        <input class="input" v-model="formData.itemTag" placeholder="如: 数码/衣物" />
      </view>
      
      <view class="form-item">
        <text class="label">备注</text>
        <textarea class="textarea" v-model="formData.itemDesc" placeholder="物品描述..." />
      </view>
    </view>
    
    <button class="submit-btn" @click="submit" :loading="submitting">确认添加</button>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { saveItem } from '@/api/index';
import type { ItemDTO } from '@/common/types';

const submitting = ref(false);

// 初始化表单，符合 ItemDTO 结构
const formData = reactive<ItemDTO>({
  id: 0, // 后端生成
  itemCode: `ITM_${Date.now()}`, // 模拟自动生成编码
  itemName: '',
  boxId: 1, // 默认值
  price: 0,
  itemTag: '',
  itemDesc: '',
  isValid: 1
});

const submit = async () => {
  if (!formData.itemName || !formData.boxId) {
    uni.showToast({ title: '请填写必填项', icon: 'none' });
    return;
  }

  submitting.value = true;
  try {
    // POST /api/items/add
    const res = await saveItem(formData);
    if (res.code === 200) {
      uni.showToast({ title: '添加成功', icon: 'success' });
      setTimeout(() => uni.navigateBack(), 1500);
    }
  } catch (e) {
    console.error(e);
    uni.showToast({ title: '添加失败', icon: 'none' });
  } finally {
    submitting.value = false;
  }
};
</script>

<style lang="scss">
.container { padding: 20px; background: #f8f8f8; min-height: 100vh; }
.form-group { background: #fff; border-radius: 10px; padding: 0 15px; margin-bottom: 20px; }
.form-item {
  border-bottom: 1px solid #eee; padding: 15px 0;
  &:last-child { border-bottom: none; }
  .label { display: block; margin-bottom: 8px; font-size: 14px; color: #333; }
  .required { color: red; margin-left: 4px; }
  .input { font-size: 16px; height: 30px; }
  .textarea { width: 100%; height: 80px; font-size: 16px; }
}
.submit-btn { background: #007aff; color: #fff; border-radius: 25px; }
</style>