# 智能收纳盒系统 API 文档

## 基本信息
- 基础URL: `http://localhost:8080`
- 版本: v1.0.0
- 描述: 智能收纳盒系统的RESTful API接口文档

## 数据模型

### BoxDTO (盒子数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 否 | 主键ID |
| boxCode | String | 是 | 盒子编码，长度不超过32个字符 |
| userId | Long | 是 | 用户ID |
| boxName | String | 是 | 盒子名称，长度不超过50个字符 |
| boxType | Integer | 是 | 箱体类型 0-子箱，1-主箱 |
| status | Integer | 是 | 盒子状态（0-离线 1-在线 2-低电 3-故障） |
| rssi | Integer | 否 | RSSI信号强度（-100到0） |
| battery | Integer | 否 | 电池电量（0-100） |
| lastHeartbeatTime | LocalDateTime | 否 | 最后心跳时间 |
| createTime | LocalDateTime | 否 | 创建时间 |
| updateTime | LocalDateTime | 否 | 更新时间 |

### ItemDTO (商品数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 否 | 主键ID |
| itemCode | String | 是 | 商品编码，长度不超过255个字符 |
| itemName | String | 是 | 商品名称，长度不超过100个字符 |
| itemDesc | String | 否 | 商品描述，长度不超过500个字符 |
| boxId | Long | 是 | 盒子ID |
| price | Double | 是 | 商品价格，必须大于0 |
| autoRecognizeName | String | 否 | 自动识别名称 |
| manualEditName | String | 否 | 手动编辑名称 |
| itemTag | String | 否 | 商品标签 |
| putInTime | LocalDateTime | 否 | 放入时间 |
| expireTime | LocalDateTime | 否 | 过期时间 |
| isValid | Integer | 否 | 是否有效 |
| createTime | LocalDateTime | 否 | 创建时间 |
| updateTime | LocalDateTime | 否 | 更新时间 |

### EmotionDTO (情感标签数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 否 | 主键ID |
| itemId | Long | 是 | 物品ID |
| emotionTag | Integer | 是 | 情感标签（1-快乐 2-悲伤 3-愤怒 4-焦虑 5-平静） |
| emotionRemark | String | 否 | 情感备注，长度不超过500个字符 |

### UserDTO (用户数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| id | Long | 否 | 主键ID |
| username | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| createdAt | LocalDateTime | 否 | 创建时间 |
| updatedAt | LocalDateTime | 否 | 更新时间 |

### AiChatRequestDTO (AI聊天请求参数DTO)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID |
| message | String | 是 | 用户发送的消息内容，长度不超过1000个字符 |

### ErrorLogDTO (错误日志数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| errorCode | String | 是 | 错误编码 |
| errorMessage | String | 是 | 错误信息 |
| errorTime | LocalDateTime | 是 | 错误时间 |

### FallbackRequestDTO (优雅降级请求数据传输对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| serviceId | String | 是 | 服务标识 |
| fallbackReason | String | 是 | 降级原因 |

### QueryBoxDTO (盒子查询条件封装对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| userId | Long | 否 | 用户ID |
| boxCode | String | 否 | 盒子编码 |
| boxName | String | 否 | 盒子名称 |
| boxType | Integer | 否 | 盒子类型 |
| status | Integer | 否 | 状态 |

### QueryItemDTO (商品查询条件封装对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| boxId | Long | 否 | 盒子ID |
| itemCode | String | 否 | 商品编码 |
| itemTag | String | 否 | 商品标签 |
| isValid | Integer | 否 | 是否有效 |

### QueryEmotionDTO (情感标签查询条件封装对象)

| 字段名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| itemId | Long | 否 | 物品ID |

## API 接口

### 盒子管理

#### 新增盒子
- **URL**: `/api/boxes/add`
- **方法**: POST
- **请求参数**: BoxDTO
- **响应**: RestResult<Void>
- **描述**: 新增盒子

#### 删除盒子
- **URL**: `/api/boxes/delete/{id}`
- **方法**: DELETE
- **请求参数**: id (路径参数)
- **响应**: RestResult<Void>
- **描述**: 删除盒子

#### 修改盒子信息
- **URL**: `/api/boxes/update`
- **方法**: PUT
- **请求参数**: BoxDTO
- **响应**: RestResult<Void>
- **描述**: 修改盒子

#### 查询盒子列表
- **URL**: `/api/boxes/list`
- **方法**: GET
- **请求参数**: QueryBoxDTO
- **响应**: RestResult<List<BoxDTO>>
- **描述**: 查询盒子列表

### 商品管理

#### 添加新商品
- **URL**: `/api/items/add`
- **方法**: POST
- **请求参数**: ItemDTO
- **响应**: RestResult<Void>
- **描述**: 创建新的商品记录

#### 删除指定商品
- **URL**: `/api/items/delete/{id}`
- **方法**: DELETE
- **请求参数**: id (路径参数)
- **响应**: RestResult<Void>
- **描述**: 删除指定的商品记录

#### 更新商品信息
- **URL**: `/api/items/update`
- **方法**: PUT
- **请求参数**: ItemDTO
- **响应**: RestResult<Void>
- **描述**: 更新商品的基本信息

#### 查询商品列表
- **URL**: `/api/items/list`
- **方法**: GET
- **请求参数**: QueryItemDTO
- **响应**: RestResult<List<ItemDTO>>
- **描述**: 根据条件查询商品列表

### 用户管理

#### 创建用户
- **URL**: `/api/users`
- **方法**: POST
- **请求参数**: UserDO
- **响应**: UserDTO
- **描述**: 创建新的用户账户

#### 根据ID获取用户
- **URL**: `/api/users/{id}`
- **方法**: GET
- **请求参数**: id (路径参数)
- **响应**: UserDTO
- **描述**: 根据用户ID获取用户详细信息

#### 获取所有用户
- **URL**: `/api/users`
- **方法**: GET
- **请求参数**: 无
- **响应**: List<UserDTO>
- **描述**: 获取系统中所有用户列表

#### 分页查询用户
- **URL**: `/api/users/page`
- **方法**: GET
- **请求参数**: UserQuery
- **响应**: List<UserDTO>
- **描述**: 分页查询用户信息

#### 更新用户
- **URL**: `/api/users/{id}`
- **方法**: PUT
- **请求参数**: id (路径参数), UserDO
- **响应**: UserDTO
- **描述**: 更新用户基本信息

#### 删除用户
- **URL**: `/api/users/{id}`
- **方法**: DELETE
- **请求参数**: id (路径参数)
- **响应**: Void
- **描述**: 删除指定的用户账户

### 情感标签管理

#### 添加情感标签
- **URL**: `/api/emotions/add`
- **方法**: POST
- **请求参数**: EmotionDTO
- **响应**: RestResult<Void>
- **描述**: 为物品添加情感标签

#### 删除情感标签
- **URL**: `/api/emotions/delete/{id}`
- **方法**: DELETE
- **请求参数**: id (路径参数)
- **响应**: RestResult<Void>
- **描述**: 删除指定的情感标签

#### 更新情感标签
- **URL**: `/api/emotions/update`
- **方法**: PUT
- **请求参数**: EmotionDTO
- **响应**: RestResult<Void>
- **描述**: 更新情感标签信息

#### 查询情感标签列表
- **URL**: `/api/emotions/list`
- **方法**: GET
- **请求参数**: QueryEmotionDTO
- **响应**: RestResult<List<EmotionDTO>>
- **描述**: 根据条件查询情感标签列表

### AI聊天

#### 处理AI聊天请求
- **URL**: `/api/chat`
- **方法**: POST
- **请求参数**: AiChatRequestDTO
- **响应**: RestResult<?>
- **描述**: 处理用户的AI聊天请求

### 错误修复管理

#### 记录运行时错误
- **URL**: `/api/error-recovery/record-error`
- **方法**: POST
- **请求参数**: ErrorLogDTO
- **响应**: RestResult<Void>
- **描述**: 记录系统运行时错误日志

#### 触发优雅降级策略
- **URL**: `/api/error-recovery/trigger-fallback`
- **方法**: POST
- **请求参数**: FallbackRequestDTO
- **响应**: RestResult<Object>
- **描述**: 在服务异常时触发降级策略