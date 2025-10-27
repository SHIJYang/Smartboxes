<template>
  <view class="container">
    <u-navbar 
      title="添加物品" 
      title-bold 
      title-color="#333" 
      :custom-style="navbarStyle"
    />

    <view class="form-container" ref="formContainer">
      <u-form 
        :model="form" 
        :rules="rules" 
        ref="uForm" 
        label-width="150rpx"
        :custom-style="formStyle"
      >
        <!-- 物品名称 -->
        <form-item-wrap>
          <u-form-item label="物品名称" prop="name" border-bottom>
            <u-input 
              v-model="form.name" 
              placeholder="请输入物品名称" 
              :custom-style="inputStyle"
              @focus="handleInputFocus($event, 'name')"
              @blur="handleInputBlur($event, 'name')"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 物品分类 -->
        <form-item-wrap>
          <u-form-item label="物品分类" prop="category" border-bottom>
            <u-input
              v-model="form.category"
              placeholder="请选择物品分类"
              disabled
              @click="showCategoryPicker = true"
              :custom-style="inputStyle"
            />
            <u-icon 
              name="arrow-right" 
              slot="right" 
              :color="activeColor"
              :size="24"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 物品描述 -->
        <form-item-wrap>
          <u-form-item label="物品描述" prop="description" border-bottom>
            <u-input
              v-model="form.description"
              type="textarea"
              placeholder="请输入物品描述"
              :height="120"
              :custom-style="inputStyle"
              @focus="handleInputFocus($event, 'description')"
              @blur="handleInputBlur($event, 'description')"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 数量 -->
        <form-item-wrap>
          <u-form-item label="数量" prop="quantity" border-bottom>
            <u-number-box 
              v-model="form.quantity" 
              :min="1" 
              :max="999" 
              :button-size="36"
              :input-width="80"
              @change="handleNumberChange"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 所在位置 -->
        <form-item-wrap>
          <u-form-item label="所在位置" prop="location" border-bottom>
            <u-input 
              v-model="form.location" 
              placeholder="请输入物品位置" 
              :custom-style="inputStyle"
              @focus="handleInputFocus($event, 'location')"
              @blur="handleInputBlur($event, 'location')"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 上传图片 -->
        <form-item-wrap>
          <u-form-item label="上传图片" prop="image" border-bottom>
            <u-upload
              :fileList="fileList"
              @afterRead="afterRead"
              @delete="deletePic"
              name="image"
              :maxCount="1"
              width="160rpx"
              height="160rpx"
              :upload-text="'点击上传'"
              :custom-style="uploadStyle"
            ></u-upload>
          </u-form-item>
        </form-item-wrap>
      </u-form>

      <view class="button-group" ref="buttonGroup">
        <u-button 
          type="primary" 
          @click="submitForm" 
          :loading="loading"
          :custom-style="primaryBtnStyle"
          class="btn-primary"
        >
          添加物品
        </u-button>
        <u-button 
          type="default" 
          @click="resetForm" 
          :custom-style="defaultBtnStyle"
          class="btn-default"
        >
          重置
        </u-button>
      </view>
    </view>

    <!-- 分类选择器 (带动画) -->
    <u-picker
      :show="showCategoryPicker"
      :columns="categoryColumns"
      keyName="label"
      @confirm="confirmCategory"
      @cancel="showCategoryPicker = false"
      :mask-opacity="0.5"
      :animation="true"
    />
  </view>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";

// 注册GSAP插件
gsap.registerPlugin(ScrollTrigger);

// 表单数据
const form = reactive({
  name: "",
  category: "",
  description: "",
  quantity: 1,
  location: "",
  image: "",
});

// 表单验证规则
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

// 状态管理
const fileList = ref([]);
const showCategoryPicker = ref(false);
const loading = ref(false);
const uForm = ref();
const formContainer = ref(null);
const buttonGroup = ref(null);
const activeColor = "#3c9cff";

// 分类数据
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

// 样式定义
const navbarStyle = {
  backgroundColor: "#fff",
  borderBottom: "1px solid #f0f0f0",
  boxShadow: "0 2px 8px rgba(0,0,0,0.05)"
};

const formStyle = {
  backgroundColor: "#fff",
  borderRadius: "16rpx",
  padding: "20rpx 0"
};

const inputStyle = {
  fontSize: "28rpx",
  color: "#333",
  height: "80rpx"
};

const uploadStyle = {
  border: "2px dashed #ddd",
  borderRadius: "8rpx",
  transition: "all 0.3s"
};

const primaryBtnStyle = {
  height: "90rpx",
  lineHeight: "90rpx",
  fontSize: "30rpx",
  borderRadius: "45rpx",
  backgroundColor: "#3c9cff",
  borderColor: "#3c9cff"
};

const defaultBtnStyle = {
  height: "90rpx",
  lineHeight: "90rpx",
  fontSize: "30rpx",
  borderRadius: "45rpx",
  marginTop: "20rpx"
};

// 生命周期 - 组件挂载后执行动画
onMounted(() => {
  // 表单元素入场动画
  const formItems = formContainer.value.querySelectorAll('.form-item-wrap');
  
  gsap.set(formItems, {
    opacity: 0,
    y: 20
  });
  
  gsap.to(formItems, {
    opacity: 1,
    y: 0,
    duration: 0.5,
    stagger: 0.1,
    ease: "power2.out"
  });

  // 按钮组入场动画
  gsap.set(buttonGroup.value, {
    opacity: 0,
    y: 30
  });
  
  gsap.to(buttonGroup.value, {
    opacity: 1,
    y: 0,
    duration: 0.6,
    delay: 0.5,
    ease: "power2.out"
  });
});

// 分类选择确认
const confirmCategory = (e) => {
  form.category = e.value[0];
  showCategoryPicker.value = false;
  
  // 选择分类后的反馈动画
  gsap.from('.form-item-wrap:nth-child(2) input', {
    scale: 0.95,
    duration: 0.3,
    ease: "elastic.out(1, 0.3)"
  });
};

// 图片上传处理
const afterRead = (event) => {
  const { file } = event;
  fileList.value.push({
    url: file.url,
    status: "success",
    message: "上传成功",
  });
  form.image = file.url;
  
  // 上传成功动画
  gsap.from('.u-upload', {
    scale: 1.1,
    opacity: 0.8,
    duration: 0.4,
    ease: "back.out(1.7)"
  });
};

// 图片删除处理
const deletePic = (event) => {
  fileList.value.splice(event.index, 1);
  form.image = "";
};

// 输入框聚焦动画
const handleInputFocus = (e, field) => {
  const inputEl = e.target;
  gsap.to(inputEl, {
    borderColor: activeColor,
    duration: 0.3,
    ease: "power1.out"
  });
};

// 输入框失焦动画
const handleInputBlur = (e, field) => {
  const inputEl = e.target;
  gsap.to(inputEl, {
    borderColor: "#ddd",
    duration: 0.3,
    ease: "power1.out"
  });
};

// 数量变化动画
const handleNumberChange = () => {
  gsap.from('.u-number-box', {
    scale: 1.05,
    duration: 0.2,
    ease: "power1.out"
  });
};

// 表单提交
const submitForm = async () => {
  try {
    const valid = await uForm.value.validate();
    if (valid) {
      loading.value = true;
      
      // 提交按钮加载动画
      gsap.to('.btn-primary', {
        scale: 0.98,
        duration: 0.2,
        yoyo: true,
        repeat: 3
      });

      // 模拟提交过程
      await new Promise((resolve) => setTimeout(resolve, 1500));

      // 提交成功动画
      uni.showToast({
        title: "添加成功",
        icon: "success",
        duration: 1500
      });

      // 返回上一页
      setTimeout(() => {
        uni.navigateBack();
      }, 1500);
    }
  } catch (error) {
    console.log("表单验证失败:", error);
    // 验证失败动画
    gsap.from(uForm.value.$el, {
      x: 10,
      duration: 0.1,
      yoyo: true,
      repeat: 3
    });
  } finally {
    loading.value = false;
  }
};

// 表单重置
const resetForm = () => {
  uForm.value.resetFields();
  fileList.value = [];
  
  // 重置动画
  gsap.from(formContainer.value, {
    opacity: 0.6,
    duration: 0.3,
    ease: "power1.out"
  });
};
</script>

<style scoped>
.container {
  background-color: #f5f7fa;
  min-height: 100vh;
  padding-bottom: 60rpx;
}

.form-container {
  padding: 30rpx 40rpx;
  margin-top: 20rpx;
}

.form-item-wrap {
  overflow: hidden;
}

.button-group {
  margin-top: 60rpx;
  padding: 0 20rpx;
}

/* 按钮悬停效果 */
.btn-primary {
  transition: all 0.3s ease;
}

.btn-primary:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 8rpx 16rpx rgba(60, 156, 255, 0.3);
}

.btn-default {
  transition: all 0.3s ease;
}

.btn-default:hover {
  transform: translateY(-2rpx);
  box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.08);
}

/* 上传组件悬停效果 */
.u-upload:hover {
  border-color: #3c9cff !important;
  background-color: rgba(60, 156, 255, 0.05);
}
</style>