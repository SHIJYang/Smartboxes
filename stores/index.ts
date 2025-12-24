// stores/index.ts
// 主入口文件，导出所有store
export { useUserStore } from './userStore';
export { useBoxStore } from './boxStore';
export { useItemStore } from './itemStore';
export { useChatStore } from './chatStore';

// 可选：创建全局store的组合函数
import { useUserStore } from './userStore';
import { useBoxStore } from './boxStore';
import { useItemStore } from './itemStore';
import { useChatStore } from './chatStore';

/**
 * 组合所有store的便捷函数
 */
export const useStores = () => {
  return {
    userStore: useUserStore(),
    boxStore: useBoxStore(),
    itemStore: useItemStore(),
    chatStore: useChatStore()
  };
};


// import { useStores } from '@/stores';

// const { userStore, boxStore, itemStore, chatStore } = useStores();