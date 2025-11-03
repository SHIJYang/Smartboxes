<template>
  <view class="chat-page" :class="{ 'platform-container': true }">
    <!-- 导航栏 -->
    <u-navbar 
      title="智能助手" 
      title-color="#333" 
      bg-color="#fff"
      border-bottom
      :left-icon="leftIcon"
      @left-click="handleBack"
      :custom-style="{ boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }"
    />

    <!-- 聊天区域 -->
    <scroll-view
      scroll-y
      class="chat-area"
      :scroll-top="scrollTop"
      :scroll-into-view="latestId"
      ref="chatScroll"
      :scroll-with-animation="true"
      @scroll="handleScroll"
    >
      <view class="chat-list">
        <!-- 系统提示 -->
        <view class="system-tip" v-if="showSystemTip">
          <text class="tip-text">{{ systemTipText }}</text>
        </view>

        <!-- 聊天消息 -->
        <view
          v-for="msg in messages"
          :key="msg.id"
          :id="'msg_' + msg.id"
          class="msg-item"
          :class="{ 'msg-self': msg.isSelf, 'msg-ai': !msg.isSelf }"
          ref="messageItems"
        >
          <!-- 头像 -->
          <u-avatar 
            :src="msg.avatar" 
            size="80"
            border="2px solid #f0f2f5"
            box-shadow="0 2px 6px rgba(0,0,0,0.05)"
            transition="all 0.3s ease"
            @click="handleAvatarClick(msg, $event)"
          />

          <view class="msg-content">
            <!-- 消息气泡 -->
            <view 
              class="msg-text"
              :style="{
                backgroundColor: msg.isSelf ? '#2979ff' : '#fff',
                color: msg.isSelf ? '#fff' : '#333',
                borderRadius: '16rpx',
                padding: '20rpx 28rpx',
                boxShadow: msg.isSelf ? '0 3rpx 12rpx rgba(41, 121, 255, 0.2)' : '0 3rpx 12rpx rgba(0, 0, 0, 0.06)',
                fontSize: '28rpx',
                lineHeight: '1.6'
              }"
            >
              {{ msg.text }}
            </view>

            <!-- 时间戳 -->
            <text class="msg-time" :class="{ 'time-self': msg.isSelf }">
              {{ formatTime(msg.timestamp || Date.now()) }}
            </text>

            <!-- 推荐物品 -->
            <view v-if="msg.items" class="recommend-items" ref="recommendItems">
              <u-card 
                v-for="(item, idx) in msg.items"
                :key="item.id"
                class="recommend-item"
                @click="viewItem(item, $event)"
                :border="false"
                :shadow="true"
                :radius="'12rpx'"
                :padding="'15rpx'"
                :custom-style="{ 
                  marginRight: '20rpx', 
                  minWidth: '140rpx',
                  transition: 'all 0.3s ease',
                  cursor: 'pointer'
                }"
              >
                <u-image
                  :src="item.image"
                  width="120rpx"
                  height="120rpx"
                  radius="'8rpx'"
                  :lazy-load="true"
                  :show-loading="true"
                  :loading-icon="loadingIcon"
                  :error-icon="errorIcon"
                  :custom-style="{ objectFit: 'cover' }"
                />
                <text 
                  class="item-name"
                  :style="{ 
                    fontSize: '24rpx', 
                    marginTop: '10rpx',
                    color: '#666'
                  }"
                >
                  {{ item.itemName }}
                </text>
              </u-card>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 输入区 -->
    <view class="input-area" :class="{ 'safe-area-bottom': true }" ref="inputArea">
      <u-input
        v-model="inputText"
        placeholder="请输入..."
        :focus="inputFocus"
        confirm-type="send"
        @confirm="sendMessage"
        ref="inputField"
        :border="false"
        :radius="'40rpx'"
        :bg-color="'#f5f7fa'"
        :padding="['20rpx', '30rpx']"
        :height="'88rpx'"
        :placeholder-style="{ color: '#999', fontSize: '26rpx' }"
        :input-style="{ fontSize: '28rpx', color: '#333' }"
        @focus="handleInputFocus"
        @blur="handleInputBlur"
      >
        <!-- 语音按钮 -->
        <template #suffix>
          <!-- #ifdef MP-WEIXIN -->
          <u-button
            class="voice-btn"
            @longpress="onVoiceStart"
            @touchend="onVoiceStop"
            ref="voiceBtn"
            shape="circle"
            size="mini"
            :bg-color="isRecording ? 'rgba(41, 121, 255, 0.2)' : 'transparent'"
            :icon="isRecording ? 'microphone-filled' : 'microphone'"
            :icon-size="48rpx"
            :icon-color="'#2979ff'"  <!-- 修复：颜色值加引号 -->
            :custom-style="{ padding: '15rpx' }"
          />
          <!-- #endif -->

          <!-- #ifndef MP-WEIXIN -->
          <u-button
            class="voice-btn"
            @click="toggleVoice"
            ref="voiceBtn"
            shape="circle"
            size="mini"
            bg-color="transparent"
            :icon="showVoice ? 'microphone-filled' : 'microphone'"
            :icon-size="'48rpx'"  
            :icon-color="'#2979ff'"
            :custom-style="{ padding: '15rpx' }"
          />
          <!-- #endif -->
        </template>
      </u-input>

      <!-- 语音录制弹窗 -->
      <u-popup 
        v-model="showVoicePopup"
        mode="center"
        :mask-opacity="0.6"
        :border-radius="'20rpx'"
        :custom-style="{ width: '300rpx', height: '300rpx', background: 'rgba(0,0,0,0.8)' }"
      >
        <view class="voice-popu-content">
          <u-icon 
            :name="isRecording ? 'microphone-filled' : 'microphone'"
            size="80rpx"
            color="#fff"
            ref="voicePopupIcon"
          />
          <text 
            :style="{ 
              color: '#fff', 
              fontSize: '28rpx', 
              marginTop: '20rpx' 
            }"
          >
            {{ isRecording ? '正在录音...' : '点击开始录音' }}
          </text>
        </view>
      </u-popup>
    </view>

    <!-- 录音提示 -->
    <u-toast 
      v-model="showRecordingToast"
      :icon="recordingIcon"
      :text="recordingText"
      :bg-color="recordingBg"
      :text-color="'#fff'"
      position="bottom"
      :custom-style="{ padding: '15rpx 20rpx', borderRadius: '10rpx' }"
    />
  </view>
</template>
<script setup>
// 1. 导入Vue核心API：用于响应式和生命周期管理
import { ref, onMounted, nextTick, watch } from "vue";
// 2. 导入GSAP动画库：用于消息入场、按钮反馈等动画效果
import { gsap } from "gsap";
import { ScrollTrigger } from "gsap/ScrollTrigger";  // GSAP滚动触发插件

// 3. 导入工具函数：HTTP请求（与后端交互）
import http from "@/utils/request";

// 4. 工具函数：时间格式化（将时间戳转为"YYYY-MM-DD HH:MM"格式）
const formatTime = (ts) => {
  try {
    const d = new Date(ts);  // 将时间戳转为Date对象
    const pad = (n) => (n < 10 ? '0' + n : '' + n);  // 补零函数：确保个位数时间显示为两位数（如9→09）
    return `${d.getFullYear()}-${pad(d.getMonth()+1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
  } catch (e) { return ''; }  // 异常处理：避免时间戳无效导致报错
};

// 5. 注册GSAP插件：启用滚动触发功能
gsap.registerPlugin(ScrollTrigger);

// 6. 静态资源配置：图标、图片等固定资源（集中管理，方便修改）
const leftIcon = ref({ name: 'arrow-left', size: 36, color: '#333' });  // 导航栏返回图标
const loadingIcon = ref('photo');  // 图片加载中图标（uView-plus内置图标名）
const errorIcon = ref('error-circle');  // 图片加载失败图标
const recordingIcon = ref('microphone-filled');  // 录音提示图标

// 7. 响应式状态管理：页面核心数据（使用ref创建响应式变量）
const messages = ref([  // 消息数组：初始包含2条默认消息（AI欢迎语+用户示例消息）
  {
    id: 1,
    text: "你好！我是收纳助手，可以帮你查找物品。",
    isSelf: false,  // 是否自己发送：false=AI，true=自己
    avatar: "/static/avatar/ai.png",  // AI头像路径
    timestamp: Date.now() - 30000  // 时间戳：30秒前（模拟历史消息）
  },
  {
    id: 2,
    text: "我想找我的钢笔",
    isSelf: true,  // 自己发送的消息
    avatar: "/static/avatar/user.png",  // 自己的头像路径
    timestamp: Date.now() - 25000  // 时间戳：25秒前
  },
]);
const inputText = ref("");  // 输入框内容：双向绑定到u-input
const inputFocus = ref(false);  // 输入框是否聚焦：控制键盘唤起
const showVoice = ref(false);  // 非微信端是否显示录音弹窗
const showVoicePopup = ref(false);  // 录音弹窗显示状态
const showRecordingToast = ref(false);  // 录音底部提示显示状态
const showSystemTip = ref(true);  // 系统提示显示状态
const systemTipText = ref("可发送物品名称快速查找");  // 系统提示文字内容
const latestId = ref("msg_2");  // 最新消息ID：用于scroll-into-view定位
const scrollTop = ref(0);  // 滚动距离：手动控制滚动位置
const isRecording = ref(false);  // 是否正在录音：控制图标和背景色变化
const recordingText = ref("正在录音...");  // 录音提示文字
const recordingBg = ref("rgba(41, 121, 255, 0.8)");  // 录音提示背景色

// 8. DOM引用：获取页面元素（用于操作DOM，如动画、滚动）
const chatScroll = ref(null);  // 滚动容器引用
const messageItems = ref([]);  // 所有消息元素引用
const recommendItems = ref(null);  // 推荐物品容器引用
const inputArea = ref(null);  // 输入区域引用
const inputField = ref(null);  // 输入框引用
const voiceBtn = ref(null);  // 语音按钮引用
const voicePopupIcon = ref(null);  // 录音弹窗图标引用

// 9. 生命周期钩子：页面挂载完成后执行（初始化动画、事件监听）
onMounted(() => {
  // 9.1 初始消息入场动画：页面加载时，消息从下往上淡入
  const initialMessages = messageItems.value;  // 获取所有初始消息元素
  gsap.set(initialMessages, { opacity: 0, y: 20, scale: 0.95 });  // 初始状态：透明、下移、缩小
  
  // 区分AI消息和自己的消息，分批次入场（增强层次感）
  const aiMessages = initialMessages.filter(item => item.classList.contains('msg-ai'));
  const selfMessages = initialMessages.filter(item => item.classList.contains('msg-self'));
  
  // AI消息先入场：0.5秒动画，间隔0.3秒依次显示
  gsap.to(aiMessages, { opacity: 1, y: 0, scale: 1, duration: 0.5, stagger: 0.3, ease: "power2.out" });
  // 自己的消息后入场：延迟0.2秒，避免与AI消息重叠
  gsap.to(selfMessages, { opacity: 1, y: 0, scale: 1, duration: 0.5, stagger: 0.3, delay: 0.2, ease: "power2.out" });

  // 9.2 输入区域入场动画：从下往上淡入（延迟0.5秒，等消息加载完成）
  gsap.from(inputArea.value, { y: 50, opacity: 0, duration: 0.6, delay: 0.5, ease: "power2.out" });
  // 9.3 系统提示入场动画：淡入+轻微缩放（延迟0.8秒，最后显示）
  gsap.from('.system-tip', { opacity: 0, scale: 0.9, duration: 0.5, delay: 0.8, ease: "power2.out" });

  // 9.4 滚动容器监听：顶部回弹效果（滚动到顶部继续下拉时触发）
  if (chatScroll.value) {
    ScrollTrigger.create({
      trigger: chatScroll.value,  // 触发元素：滚动容器
      start: "top top",           // 触发起点：容器顶部到达视口顶部
      end: "bottom bottom",       // 触发终点：容器底部到达视口底部
      onLeaveBack: () => {        // 回滚超出顶部时执行
        gsap.to(chatScroll.value, { scrollTop: 10, duration: 0.3, ease: "power1.out" });  // 回弹10px
      }
    });
  }
});

// 10. 录音相关逻辑：开始录音、停止录音、切换录音状态
/**
 * 开始录音（微信小程序端长按触发）
 */
const onVoiceStart = () => {
  isRecording.value = true;  // 标记为正在录音
  showVoicePopup.value = true;  // 显示录音弹窗
  showRecordingToast.value = true;  // 显示底部录音提示
  
  // 录音按钮脉冲动画：轻微放大，提示正在录音
  if (voiceBtn.value) {
    gsap.to(voiceBtn.value, { scale: 1.2, duration: 0.3, ease: "power1.out" });
    
    // 录音弹窗图标呼吸动画：缩放+透明度变化（循环）
    if (voicePopupIcon.value) {
      gsap.to(voicePopupIcon.value, {
        scale: 1.2, opacity: 0.8, duration: 0.8, yoyo: true, repeat: -1, ease: "sine.inOut"
      });
    }
  }
};

/**
 * 停止录音（微信小程序端松开手指触发）
 */
const onVoiceStop = () => {
  isRecording.value = false;  // 标记为停止录音
  showVoicePopup.value = false;  // 隐藏录音弹窗
  showRecordingToast.value = false;  // 隐藏底部提示
  
  // 停止按钮动画：恢复原大小
  if (voiceBtn.value) {
    gsap.killTweensOf(voiceBtn.value);  // 清除未完成的动画
    gsap.to(voiceBtn.value, { scale: 1, duration: 0.3, ease: "power1.out" });
  }
  // 停止弹窗图标动画
  if (voicePopupIcon.value) {
    gsap.killTweensOf(voicePopupIcon.value);
  }
};

// 11. 核心功能：发送消息（文本消息）
const sendMessage = async () => {
  const content = inputText.value.trim();  // 获取输入框内容，去除前后空格
  if (!content) return;  // 内容为空时，不发送消息

  // 11.1 添加自己发送的消息到消息数组
  const msgId = Date.now();  // 用时间戳作为消息ID（确保唯一）
  messages.value.push({
    id: msgId,
    text: content,
    isSelf: true,
    avatar: "/static/avatar/user.png",
    timestamp: msgId  // 消息发送时间：当前时间戳
  });

  // 11.2 自动滚动到最新消息
  latestId.value = "msg_" + msgId;
  inputText.value = "";  // 清空输入框

  // 11.3 自己消息的入场动画：从右侧滑入（与AI消息区分）
  await nextTick();  // 等待DOM更新：确保新消息已渲染
  const newMsg = messageItems.value[messageItems.value.length - 1];  // 获取最新消息元素
  gsap.from(newMsg, { opacity: 0, x: 80, scale: 0.92, duration: 0.4, ease: "power2.out" });

  try{
    // 11.4 显示"正在思考"提示：模拟AI思考过程（提升用户体验）
    showSystemTip.value = true;
    systemTipText.value = "正在思考...";
    await new Promise(resolve => setTimeout(resolve, 800));  // 延迟800ms：避免回复太快不真实
    
    // 11.5 调用后端接口：发送消息到服务器，获取AI回复
    const data = await http.post('/chat', { message: content });
    showSystemTip.value = false;  // 隐藏"正在思考"提示
    
    // 11.6 解析后端返回数据（兼容不同返回格式：直接返回或嵌套在data中）
    const reply = data.reply ?? data?.data?.reply ?? '已收到';  // AI回复内容
    const items = data.items ?? data?.data?.items ?? [];  // 推荐物品列表
    const aiMsgId = Date.now();  // AI消息ID（时间戳）
    
    // 11.7 添加AI回复到消息数组
    messages.value.push({
      id: aiMsgId,
      text: reply,
      items,  // 推荐物品（可选）
      isSelf: false,
      avatar: "/static/avatar/ai.png",
      timestamp: aiMsgId
    });

    // 11.8 AI消息的入场动画：从左侧滑入
    await nextTick();  // 等待DOM更新
    const aiMsg = messageItems.value[messageItems.value.length - 1];  // 获取AI消息元素
    gsap.from(aiMsg, { opacity: 0, x: -80, scale: 0.92, duration: 0.4, ease: "power2.out" });

    // 11.9 推荐物品动画：弹性入场（若有推荐物品）
    if (items && items.length) {
      await nextTick();  // 等待推荐物品DOM渲染
      // 处理推荐物品容器（兼容数组和单个元素）
      const containers = Array.isArray(recommendItems.value) ? recommendItems.value : [recommendItems.value];
      // 获取所有推荐物品元素
      const itemsEls = containers.filter(Boolean).flatMap((c) => Array.from(c.querySelectorAll('.recommend-item')));
      
      if (itemsEls && itemsEls.length) {
        gsap.set(itemsEls, { opacity: 0, y: 30, scale: 0.85 });  // 初始状态：透明、下移、缩小
        // 弹性动画：0.5秒，间隔0.18秒依次显示，带回弹效果
        gsap.to(itemsEls, { opacity: 1, y: 0, scale: 1, duration: 0.5, stagger: 0.18, delay: 0.2, ease: "back.out(1.4)" });
      }
    }

    // 11.10 滚动到AI回复（最新消息）
    latestId.value = "msg_" + aiMsgId;

  } catch (e) {
    // 11.11 异常处理：发送失败时显示提示
    console.error(e);  // 打印错误日志：方便调试
    showSystemTip.value = true;
    systemTipText.value = "发送失败，请重试";
    // 错误提示动画：左右抖动，提示用户注意
    gsap.from('.system-tip', { x: 20, duration: 0.2, yoyo: true, repeat: 2, ease: "power1.inOut" });
  }
};

// 12. 非微信端：切换录音状态（点击语音按钮显示/隐藏弹窗）
const toggleVoice = () => {
  showVoice.value = !showVoice.value;  // 切换录音状态标记
  showVoicePopup.value = showVoice.value;  // 同步弹窗显示状态
  
  // 语音按钮旋转动画：点击时旋转30度，增强交互反馈
  if (voiceBtn.value && voicePopupIcon.value) {
    gsap.from(voiceBtn.value, { rotation: 30, scale: 1.3, duration: 0.3, ease: "back.out(1.5)" });
    // 弹窗图标淡入动画：缩放+透明度变化
    gsap.from(voicePopupIcon.value, { scale: 0.8, opacity: 0, duration: 0.4, ease: "back.out(1.2)" });
  }
};

// 13. 推荐物品点击事件：查看物品详情（当前仅弹窗提示，可扩展为路由跳转）
const viewItem = (item, event) => {
  const activeItem = event.currentTarget;  // 获取点击的物品元素
  // 点击反馈动画：轻微缩小再恢复（模拟按钮按压）
  gsap.to(activeItem, { scale: 0.93, duration: 0.15, yoyo: true, repeat: 1, ease: "power1.inOut" });
  
  // 打印物品信息（调试用），实际项目可替换为路由跳转（如uni.navigateTo）
  console.log("查看物品:", item);
  // 显示提示：告知用户已触发查看操作
  uni.showToast({ title: `查看${item.itemName}详情`, icon: "none", duration: 1500 });
};

// 14. 辅助交互函数：返回上一页、头像点击、输入框聚焦/失焦等
/**
 * 导航栏返回按钮：带动画返回上一页
 */
const handleBack = () => {
  // 滚动容器淡出动画：0.3秒后执行返回操作
  gsap.to(chatScroll.value, { 
    opacity: 0, 
    duration: 0.3, 
    ease: "power1.out", 
    onComplete: () => uni.navigateBack()  // 动画结束后返回上一页
  });
};

/**
 * 头像点击事件：显示用户身份提示
 * @param {Object} msg - 当前消息数据（包含isSelf标记）
 * @param {Event} event - 点击事件对象
 */
const handleAvatarClick = (msg, event) => {
  const avatar = event.currentTarget;  // 获取点击的头像元素
  // 头像缩放动画：轻微放大再恢复（反馈点击）
  gsap.to(avatar, { scale: 1.1, duration: 0.2, yoyo: true, repeat: 1, ease: "power1.inOut" });
  
  // 根据消息类型显示不同提示（自己/AI）
  const tip = msg.isSelf ? "这是你" : "智能助手";
  uni.showToast({ title: tip, icon: "none" });  // 显示轻提示
};

/**
 * 输入框聚焦事件：输入框放大+背景色变化（增强交互）
 */
const handleInputFocus = () => {
  gsap.to(inputField.value.$el, {
    scale: 1.02,  // 放大2%：提示聚焦状态
    backgroundColor: "#fff",  // 背景色变白：区分聚焦状态
    boxShadow: "0 4rpx 16rpx rgba(0,0,0,0.08)",  // 显示阴影：增强层次感
    duration: 0.3,
    ease: "power1.out"
  });
};

/**
 * 输入框失焦事件：恢复原大小和背景色
 */
const handleInputBlur = () => {
  gsap.to(inputField.value.$el, {
    scale: 1,  // 恢复原大小
    backgroundColor: "#f5f7fa",  // 恢复浅灰背景
    boxShadow: "none",  // 隐藏阴影
    duration: 0.3,
    ease: "power1.out"
  });
};

/**
 * 滚动事件：滚动聊天区域时，隐藏系统提示（避免遮挡消息）
 */
const handleScroll = () => {
  showSystemTip.value = false;
};

// 15. 监听录音状态变化：动态更新录音提示文字和背景色
watch(isRecording, (newVal) => {
  if (newVal) {
    // 正在录音：蓝色背景+"正在录音..."文字
    recordingText.value = "正在录音...";
    recordingBg.value = "rgba(41, 121, 255, 0.8)";
  } else {
    // 停止录音：绿色背景+"录音已结束"文字
    recordingText.value = "录音已结束";
    recordingBg.value = "rgba(67, 191, 86, 0.8)";
    // 1.5秒后隐藏提示：避免长期占用屏幕
    setTimeout(() => {
      showRecordingToast.value = false;
    }, 1500);
  }
});
</script>

<style scoped>
.chat-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.chat-area {
  flex: 1;
  padding: 20rpx;
  overflow: hidden;
}

.chat-list {
  min-height: 100%;
  padding-bottom: 20rpx;
}

.system-tip {
  display: flex;
  justify-content: center;
  margin: 20rpx 0;
}

.tip-text {
  padding: 8rpx 20rpx;
  background-color: rgba(0,0,0,0.05);
  color: #999;
  font-size: 24rpx;
  border-radius: 20rpx;
}

.msg-item {
  display: flex;
  margin: 30rpx 0;
  padding: 0 10rpx;
  position: relative;
  align-items: flex-start;
}

.msg-self {
  flex-direction: row-reverse;
}

.msg-content {
  max-width: 72%;
  margin: 0 20rpx;
  display: flex;
  flex-direction: column;
}

.msg-text {
  max-width: 100%;
  word-wrap: break-word;
  word-break: break-all;
}

.msg-time {
  font-size: 22rpx;
  color: #999;
  margin-top: 8rpx;
  margin-left: 12rpx;
  display: inline-block;
}

.time-self {
  margin-left: 0;
  margin-right: 12rpx;
  text-align: right;
}

.recommend-items {
  display: flex;
  margin-top: 20rpx;
  padding: 10rpx 0;
  overflow-x: auto;
  scrollbar-width: none;
  gap: 15rpx;
}

.recommend-items::-webkit-scrollbar {
  display: none;
}

.item-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.input-area {
  display: flex;
  align-items: center;
  padding: 16rpx 20rpx;
  background-color: #fff;
  border-top: 1px solid #f0f2f5;
}

.voice-popu-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.safe-area-bottom {
  padding-bottom: env(safe-area-inset-bottom);
}

@media (prefers-color-scheme: dark) {
  .chat-page {
    background-color: #1a1a1a;
  }
  
  .msg-ai .msg-text {
    background: #2d2d2d !important;
    color: #eee !important;
  }
  
  .u-input {
    background-color: #2d2d2d !important;
  }
  
  .input-area {
    background-color: #1a1a1a;
    border-top-color: #2d2d2d;
  }
  
  .tip-text {
    background-color: rgba(255,255,255,0.1);
    color: #ccc;
  }
}
</style>