// stores/boxStore.ts
import { defineStore } from 'pinia';
import * as API from '@/api';
import type { BoxDTO, QueryBoxDTO } from '@/common/types';

export const useBoxStore = defineStore('box', {
  state: () => ({
    // 盒子列表
    boxList: [] as BoxDTO[],
    
    // 当前选中的盒子详情
    currentBox: null as BoxDTO | null,
    
    // 分页信息
    pagination: {
      currentPage: 1,
      pageSize: 10,
      total: 0
    },
    
    // 查询参数
    queryParams: {
      boxCode: '',
      boxName: '',
      boxType: undefined as number | undefined,
      status: undefined as number | undefined
    } as QueryBoxDTO,
    
    // 加载状态
    loading: false,
    detailLoading: false,
    
    // 选中盒子ID（用于详情页）
    selectedBoxId: null as number | null
  }),

  getters: {
    // 主盒列表
    mainBoxes: (state) => state.boxList.filter(box => box.boxType === 1),
    
    // 子盒列表
    subBoxes: (state) => state.boxList.filter(box => box.boxType === 0),
    
    // 在线盒子
    onlineBoxes: (state) => state.boxList.filter(box => box.status === 1),
    
    // 离线盒子
    offlineBoxes: (state) => state.boxList.filter(box => box.status === 0),
    
    // 根据ID获取盒子
    getBoxById: (state) => (id: number) => {
      return state.boxList.find(box => box.id === id) || null;
    },
    
    // 获取当前用户的所有盒子
    getUserBoxes: (state) => (userId: number) => {
      return state.boxList.filter(box => box.userId === userId);
    }
  },

  actions: {
    // 获取盒子列表
    async fetchBoxList(params?: QueryBoxDTO) {
      this.loading = true;
      try {
        const response = await API.getBoxList(params || this.queryParams);
        this.boxList = response.data || [];
        
        // 如果有分页信息
        if (response.data && response.data.pagination) {
          this.pagination = {
            currentPage: response.data.currentPage || 1,
            pageSize: response.data.pageSize || 10,
            total: response.data.total || 0
          };
        }
        
        return this.boxList;
      } catch (error) {
        console.error('获取盒子列表失败:', error);
        throw error;
      } finally {
        this.loading = false;
      }
    },

    // 获取盒子详情
    async fetchBoxDetail(id: number) {
      this.detailLoading = true;
      this.selectedBoxId = id;
      
      try {
        const response = await API.getBoxDetail(id);
        this.currentBox = response.data;
        return response.data;
      } catch (error) {
        console.error(`获取盒子详情失败 (ID: ${id}):`, error);
        throw error;
      } finally {
        this.detailLoading = false;
      }
    },

    // 创建或更新盒子
    async saveBox(boxData: BoxDTO) {
      this.loading = true;
      try {
        await API.saveBox(boxData);
        
        // 更新本地数据
        if (boxData.id) {
          // 更新操作
          const index = this.boxList.findIndex(box => box.id === boxData.id);
          if (index !== -1) {
            this.boxList[index] = { ...this.boxList[index], ...boxData };
          }
          
          // 如果当前查看的就是这个盒子，也更新
          if (this.currentBox?.id === boxData.id) {
            this.currentBox = { ...this.currentBox, ...boxData };
          }
        } else {
          // 创建操作 - 重新获取列表
          await this.fetchBoxList();
        }
        
        return { success: true };
      } catch (error) {
        console.error('保存盒子失败:', error);
        return { success: false, message: '保存失败' };
      } finally {
        this.loading = false;
      }
    },

    // 删除盒子
    async deleteBox(id: number) {
      this.loading = true;
      try {
        await API.deleteBox(id);
        
        // 从列表中移除
        this.boxList = this.boxList.filter(box => box.id !== id);
        
        // 如果删除的是当前查看的盒子，清空currentBox
        if (this.currentBox?.id === id) {
          this.currentBox = null;
        }
        
        return { success: true };
      } catch (error) {
        console.error(`删除盒子失败 (ID: ${id}):`, error);
        return { success: false, message: '删除失败' };
      } finally {
        this.loading = false;
      }
    },

    // 更新查询参数
    updateQueryParams(params: Partial<QueryBoxDTO>) {
      this.queryParams = { ...this.queryParams, ...params };
    },

    // 重置查询参数
    resetQueryParams() {
      this.queryParams = {
        boxCode: '',
        boxName: '',
        boxType: undefined,
        status: undefined
      };
    },

    // 清空当前盒子
    clearCurrentBox() {
      this.currentBox = null;
      this.selectedBoxId = null;
    },

    // 更新盒子状态（模拟实时更新）
    updateBoxStatus(id: number, status: number, rssi?: number, battery?: number) {
      const box = this.boxList.find(box => box.id === id);
      if (box) {
        box.status = status;
        if (rssi !== undefined) box.rssi = rssi;
        if (battery !== undefined) box.battery = battery;
        box.lastHeartbeatTime = new Date().toISOString();
        
        // 如果当前查看的就是这个盒子，也更新
        if (this.currentBox?.id === id) {
          this.currentBox = { ...this.currentBox, status, rssi, battery, lastHeartbeatTime: box.lastHeartbeatTime };
        }
      }
    }
  }
});