// common/types.ts

// 基础响应结构
export interface RestResult<T> {
  code: number;
  message: string;
  data: T;
}

// 盒子 (BoxDTO)
export interface BoxDTO {
  id: number;
  boxCode: string;
  userId: number;
  boxName: string;
  boxType: number; // 0-子箱，1-主箱
  status: number;  // 0-离线 1-在线 2-低电 3-故障
  battery?: number;
}

// 商品 (ItemDTO)
export interface ItemDTO {
  id: number;
  boxId: number;   // 关联的盒子ID
  itemCode: string;
  itemName: string;
  itemDesc?: string;
  itemTag?: string; // 分类标签
  price?: number;
  isValid: number; // 1-有效
  expireTime?: string;
  createTime?: string;
}

// 用户 (UserDTO)
export interface UserDTO {
  id: number;
  username: string;
  email?: string;
  avatar?: string;
}

// 情感标签 (EmotionDTO)
export interface EmotionDTO {
  id: number;
  itemId: number;
  emotionTag: number; // 1-快乐 2-悲伤 3-愤怒 4-焦虑 5-平静
  emotionRemark?: string;
}

// AI 聊天请求
export interface AiChatRequestDTO {
  userId: number;
  message: string;
}

// AI 聊天响应数据 (RestResultObject data)
export interface AiChatResponse {
  reply: string;    // AI回复内容
  action?: string;  // 建议操作 (如: "OPEN_BOX_001")
}