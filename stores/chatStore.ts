// stores/chatStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { AiChatRequestDTO, AiChatResponse } from '@/common/types';

export const useChatStore = defineStore('chat', {
  state: () => ({
    // 聊天记录
    messages: [] as Array<{
      id: number;
      role: 'user' | 'assistant';
      content: string;
      timestamp: string;
      action?: string;
    }>,
    
    // 当前用户输入
    currentInput: '',
    
    // 加载状态
    loading: false,
    
    // 是否正在思考
    isThinking: false,
    
    // 聊天历史（可选）
    chatHistory: [] as Array<{
      sessionId: string;
      title: string;
      lastMessage: string;
      timestamp: string;
      messageCount: number;
    }>,
    
    // 当前会话ID
    currentSessionId: 'default',
    
    // 是否启用AI功能
    aiEnabled: true,
    
    // 错误信息
    error: null as string | null
  }),

  getters: {
    // 获取最后一条消息
    lastMessage: (state) => state.messages[state.messages.length - 1],
    
    // 获取用户消息数量
    userMessageCount: (state) => state.messages.filter(msg => msg.role === 'user').length,
    
    // 获取助手消息数量
    assistantMessageCount: (state) => state.messages.filter(msg => msg.role === 'assistant').length,
    
    // 是否为空对话
    isEmptyChat: (state) => state.messages.length === 0,
    
    // 格式化显示时间
    formattedMessages: (state) => {
      return state.messages.map(msg => ({
        ...msg,
        timeDisplay: new Date(msg.timestamp).toLocaleTimeString([], { 
          hour: '2-digit', 
          minute: '2-digit' 
        })
      }));
    }
  },

  actions: {
    // 发送消息
    async sendMessage(message: string, userId: number) {
      if (!message.trim() || this.loading) return;
      
      // 添加用户消息
      const userMessage = {
        id: Date.now(),
        role: 'user' as const,
        content: message,
        timestamp: new Date().toISOString()
      };
      
      this.messages.push(userMessage);
      this.currentInput = '';
      this.loading = true;
      this.isThinking = true;
      this.error = null;
      
      try {
        // 调用AI聊天接口
        const response = await API.sendChat({
          userId,
          message
        });
        
        // 添加AI回复
        const aiMessage = {
          id: Date.now() + 1,
          role: 'assistant' as const,
          content: response.data.reply,
          action: response.data.action,
          timestamp: new Date().toISOString()
        };
        
        this.messages.push(aiMessage);
        
        // 如果有特殊action，可以触发相应操作
        if (response.data.action) {
          this.handleAction(response.data.action);
        }
        
        return response.data;
      } catch (error) {
        console.error('发送消息失败:', error);
        this.error = '发送消息失败，请稍后重试';
        
        // 添加错误消息
        const errorMessage = {
          id: Date.now() + 1,
          role: 'assistant' as const,
          content: '抱歉，我暂时无法处理您的请求。请稍后再试。',
          timestamp: new Date().toISOString()
        };
        
        this.messages.push(errorMessage);
        
        throw error;
      } finally {
        this.loading = false;
        this.isThinking = false;
      }
    },

    // 处理AI返回的action
    handleAction(action: string) {
      console.log('处理AI动作:', action);
      
      // 根据action执行相应操作
      switch (action) {
        case 'show_items':
          // 触发显示物品列表
          break;
        case 'add_item':
          // 触发添加物品
          break;
        case 'check_status':
          // 触发检查状态
          break;
        default:
          console.log('未知动作:', action);
      }
    },

    // 清空聊天记录
    clearMessages() {
      this.messages = [];
      this.error = null;
    },

    // 设置当前输入
    setCurrentInput(input: string) {
      this.currentInput = input;
    },

    // 加载历史会话
    loadSession(sessionId: string) {
      // 这里可以从本地存储或API加载历史会话
      console.log('加载会话:', sessionId);
      this.currentSessionId = sessionId;
      
      // 模拟加载
      this.messages = [
        {
          id: 1,
          role: 'user',
          content: '你好',
          timestamp: new Date(Date.now() - 3600000).toISOString()
        },
        {
          id: 2,
          role: 'assistant',
          content: '你好！我是您的智能盒子助手，有什么可以帮您？',
          timestamp: new Date(Date.now() - 3590000).toISOString()
        }
      ];
    },

    // 创建新会话
    createNewSession() {
      const sessionId = `session_${Date.now()}`;
      this.currentSessionId = sessionId;
      this.clearMessages();
      
      // 添加到历史记录
      this.chatHistory.unshift({
        sessionId,
        title: '新对话',
        lastMessage: '',
        timestamp: new Date().toISOString(),
        messageCount: 0
      });
      
      return sessionId;
    },

    // 删除会话
    deleteSession(sessionId: string) {
      this.chatHistory = this.chatHistory.filter(session => session.sessionId !== sessionId);
      if (this.currentSessionId === sessionId) {
        this.createNewSession();
      }
    },

    // 切换AI功能状态
    toggleAI() {
      this.aiEnabled = !this.aiEnabled;
    },

    // 模拟AI思考（用于演示）
    simulateThinking(duration: number = 1000) {
      return new Promise(resolve => {
        this.isThinking = true;
        setTimeout(() => {
          this.isThinking = false;
          resolve(null);
        }, duration);
      });
    }
  }
});