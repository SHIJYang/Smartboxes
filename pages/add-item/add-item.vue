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
        <!-- 收纳盒选择 -->
        <form-item-wrap>
          <u-form-item label="收纳盒" prop="box_id" border-bottom>
            <u-input
              v-model="boxDisplayName"
              placeholder="请选择收纳盒"
              disabled
              @click="showBoxPicker = true"
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

        <!-- 物品名称 -->
        <form-item-wrap>
          <u-form-item label="物品名称" prop="manual_edit_name" border-bottom>
            <u-input 
              v-model="form.manual_edit_name" 
              placeholder="请输入物品名称" 
              :custom-style="inputStyle"
              @focus="handleInputFocus($event, 'manual_edit_name')"
              @blur="handleInputBlur($event, 'manual_edit_name')"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 物品分类 -->
        <form-item-wrap>
          <u-form-item label="物品分类" prop="item_tag" border-bottom>
            <u-input
              v-model="form.item_tag"
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
          <u-form-item label="物品描述" prop="item_desc" border-bottom>
            <u-input
              v-model="form.item_desc"
              type="textarea"
              placeholder="请输入物品描述"
              :height="120"
              :custom-style="inputStyle"
              @focus="handleInputFocus($event, 'item_desc')"
              @blur="handleInputBlur($event, 'item_desc')"
            />
          </u-form-item>
        </form-item-wrap>

        <!-- 到期时间 -->
        <form-item-wrap>
          <u-form-item label="到期时间" prop="expire_time" border-bottom>
            <u-input
              v-model="form.expire_time"
              placeholder="请选择到期时间（可选）"
              disabled
              @click="showDatePicker = true"
              :custom-style="inputStyle"
            />
            <u-icon 
              name="calendar" 
              slot="right" 
              :color="activeColor"
              :size="24"
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

    <!-- 收纳盒选择器 -->
    <u-picker
      :show="showBoxPicker"
      :columns="boxColumns"
      keyName="label"
      @confirm="confirmBox"
      @cancel="showBoxPicker = false"
      :mask-opacity="0.5"
      :animation="true"
    />

    <!-- 分类选择器 -->
    <u-picker
      :show="showCategoryPicker"
      :columns="categoryColumns"
      keyName="label"
      @confirm="confirmCategory"
      @cancel="showCategoryPicker = false"
      :mask-opacity="0.5"
      :animation="true"
    />

    <!-- 日期选择器 -->
    <u-datetime-picker
      :show="showDatePicker"
      mode="date"
      @confirm="confirmDate"
      @cancel="showDatePicker = false"
    />
  </view>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { onLoad } from "@dcloudio/uni-app";
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";

// 注册GSAP插件
gsap.registerPlugin(ScrollTrigger);

// 表单数据
const form = reactive({
  item_code: generateItemCode(),
  box_id: "",
  auto_recognize_name: "",
  manual_edit_name: "",
  item_tag: "",
  item_desc: "",
  put_in_time: new Date().toISOString(),
  expire_time: "",
  is_valid: 1,
});

// 表单验证规则
const rules = {
  box_id: [
    { required: true, message: "请选择收纳盒", trigger: "change" }
  ],
  manual_edit_name: [
    { required: true, message: "请输入物品名称", trigger: "blur" },
    { min: 1, max: 20, message: "物品名称长度在1-20个字符", trigger: "blur" },
  ],
  item_tag: [
    { required: true, message: "请选择物品分类", trigger: "change" }
  ],
  item_desc: [
    { max: 200, message: "描述不能超过200个字符", trigger: "blur" },
  ],
};

// 状态管理
const fileList = ref([]);
const showBoxPicker = ref(false);
const showCategoryPicker = ref(false);
const showDatePicker = ref(false);
const loading = ref(false);
const uForm = ref();
const formContainer = ref(null);
const buttonGroup = ref(null);
const activeColor = "#3c9cff";

// 模拟收纳盒数据
const boxList = ref([
  { id: 1, box_name: "书房主收纳盒", box_code: "BOX001" },
  { id: 2, box_name: "卧室子收纳盒", box_code: "BOX002" },
]);

// 计算属性：显示选中的收纳盒名称
const boxDisplayName = computed(() => {
  if (!form.box_id) return "";
  const box = boxList.value.find(item => item.id == form.box_id);
  return box ? `${box.box_name} (${box.box_code})` : "";
});

// 收纳盒选择列
const boxColumns = computed(() => [
  boxList.value.map(box => ({
    label: `${box.box_name} (${box.box_code})`,
    value: box.id
  }))
]);

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

// 生成物品编码
const generateItemCode = () => {
  const timestamp = new Date().getTime();
  const random = Math.random().toString(36).substr(2, 6).toUpperCase();
  return `ITEM-${timestamp}-${random}`;
};

// 在onLoad中获取传递的参数
onLoad((options) => {
  if (options.box_id) {
    form.box_id = options.box_id;
  }
});

// 生命周期 - 组件挂载后执行动画
onMounted(() => {
  // 表单元素入场动画
  const formItems = formContainer.value?.querySelectorAll('.form-item-wrap');
  if (formItems) {
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
  }

  // 按钮组入场动画
  if (buttonGroup.value) {
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
  }
});

// 收纳盒选择确认
const confirmBox = (e) => {
  form.box_id = e.value[0];
  showBoxPicker.value = false;
  
  // 选择后的反馈动画
  gsap.from('.form-item-wrap:nth-child(1) input', {
    scale: 0.95,
    duration: 0.3,
    ease: "elastic.out(1, 0.3)"
  });
};

// 分类选择确认
const confirmCategory = (e) => {
  form.item_tag = e.value[0];
  showCategoryPicker.value = false;
  
  // 选择后的反馈动画
  gsap.from('.form-item-wrap:nth-child(3) input', {
    scale: 0.95,
    duration: 0.3,
    ease: "elastic.out(1, 0.3)"
  });
};

// 日期选择确认
const confirmDate = (e) => {
  form.expire_time = e.value;
  showDatePicker.value = false;
};

// 图片上传处理
const afterRead = (event) => {
  const { file } = event;
  fileList.value.push({
    url: file.url,
    status: "success",
    message: "上传成功",
  });
  
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

      // 准备提交数据
      const submitData = {
        ...form,
        // 如果没有手动编辑名称，使用自动识别名称
        auto_recognize_name: form.auto_recognize_name || form.manual_edit_name,
      };

      console.log("提交数据:", submitData);

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
    if (uForm.value?.$el) {
      gsap.from(uForm.value.$el, {
        x: 10,
        duration: 0.1,
        yoyo: true,
        repeat: 3
      });
    }
  } finally {
    loading.value = false;
  }
};

// 表单重置
const resetForm = () => {
  if (uForm.value) {
    uForm.value.resetFields();
  }
  fileList.value = [];
  form.item_code = generateItemCode();
  form.put_in_time = new Date().toISOString();
  
  // 重置动画
  if (formContainer.value) {
    gsap.from(formContainer.value, {
      opacity: 0.6,
      duration: 0.3,
      ease: "power1.out"
    });
  }
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