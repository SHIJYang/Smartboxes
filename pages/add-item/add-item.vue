<template>
  <view class="container">
    <u-navbar title="添加物品" title-bold title-color="#333" />

    <view class="form-container">
      <u-form :model="form" :rules="rules" ref="uForm" label-width="150rpx">
        <u-form-item label="物品名称" prop="name" border-bottom>
          <u-input v-model="form.name" placeholder="请输入物品名称" />
        </u-form-item>

        <u-form-item label="物品分类" prop="category" border-bottom>
          <u-input
            v-model="form.category"
            placeholder="请选择物品分类"
            disabled
            @click="showCategoryPicker = true"
          />
          <u-icon name="arrow-right" slot="right" />
        </u-form-item>

        <u-form-item label="物品描述" prop="description" border-bottom>
          <u-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入物品描述"
            :height="120"
          />
        </u-form-item>

        <u-form-item label="数量" prop="quantity" border-bottom>
          <u-number-box v-model="form.quantity" :min="1" :max="999" />
        </u-form-item>

        <u-form-item label="所在位置" prop="location" border-bottom>
          <u-input v-model="form.location" placeholder="请输入物品位置" />
        </u-form-item>

        <u-form-item label="上传图片" prop="image" border-bottom>
          <u-upload
            :fileList="fileList"
            @afterRead="afterRead"
            @delete="deletePic"
            name="image"
            :maxCount="1"
            width="160rpx"
            height="160rpx"
          ></u-upload>
        </u-form-item>
      </u-form>

      <view class="button-group">
        <u-button type="primary" @click="submitForm" :loading="loading">
          添加物品
        </u-button>
        <u-button type="default" @click="resetForm" style="margin-top: 20rpx">
          重置
        </u-button>
      </view>
    </view>

    <!-- 分类选择器 -->
    <u-picker
      :show="showCategoryPicker"
      :columns="categoryColumns"
      keyName="label"
      @confirm="confirmCategory"
      @cancel="showCategoryPicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive } from "vue";

const form = reactive({
  name: "",
  category: "",
  description: "",
  quantity: 1,
  location: "",
  image: "",
});

const rules = {
  name: [
    { required: true, message: "请输入物品名称", trigger: "blur" },
    { min: 1, max: 20, message: "物品名称长度在1-20个字符", trigger: "blur" },
  ],
  category: [{ required: true, message: "请选择物品分类", trigger: "change" }],
  description: [
    { max: 200, message: "描述不能超过200个字符", trigger: "blur" },
  ],
  quantity: [{ required: true, message: "请输入数量", trigger: "blur" }],
  location: [{ required: true, message: "请输入物品位置", trigger: "blur" }],
};

const fileList = ref([]);
const showCategoryPicker = ref(false);
const loading = ref(false);
const uForm = ref();

const categoryColumns = ref([
  [
    { label: "工具", value: "工具" },
    { label: "文具", value: "文具" },
    { label: "衣物", value: "衣物" },
    { label: "书籍", value: "书籍" },
    { label: "电子", value: "电子" },
    { label: "食品", value: "食品" },
    { label: "药品", value: "药品" },
    { label: "其他", value: "其他" },
  ],
]);

const confirmCategory = (e) => {
  form.category = e.value[0];
  showCategoryPicker.value = false;
};

const afterRead = (event) => {
  // 这里应该处理图片上传逻辑
  // 模拟上传成功
  const { file } = event;
  fileList.value.push({
    url: file.url,
    status: "success",
    message: "上传成功",
  });
  form.image = file.url;
};

const deletePic = (event) => {
  fileList.value.splice(event.index, 1);
  form.image = "";
};

const submitForm = async () => {
  try {
    const valid = await uForm.value.validate();
    if (valid) {
      loading.value = true;

      // 模拟提交过程
      await new Promise((resolve) => setTimeout(resolve, 1000));

      uni.showToast({
        title: "添加成功",
        icon: "success",
      });

      // 返回上一页
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  } catch (error) {
    console.log("表单验证失败:", error);
  } finally {
    loading.value = false;
  }
};

const resetForm = () => {
  uForm.value.resetFields();
  fileList.value = [];
};
</script>

<style scoped>
.container {
  background-color: #f8f9fa;
  min-height: 100vh;
}

.form-container {
  padding: 40rpx;
}

.button-group {
  margin-top: 60rpx;
  padding: 0 20rpx;
}
</style>
