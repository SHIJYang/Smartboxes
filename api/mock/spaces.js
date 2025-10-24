// 模拟收纳空间数据（后续替换为 API 请求）
export const fetchSpaces = () => {
  // 模拟网络延迟
  return new Promise((resolve) => {
    
      const data = [
        { id: 1, name: '客厅抽屉', category: 'drawer', itemCount: 12 },
        { id: 2, name: '书房书架', category: 'shelf', itemCount: 8 },
        { id: 3, name: '卧室衣柜', category: 'wardrobe', itemCount: 23 },
        { id: 4, name: '工具箱', category: 'box', itemCount: 5 }
      ]
      resolve(data)
    
  })
}

// 图标映射（可后续移至 constants.js）
export const getCategoryIcon = (category) => {
  const icons = {
    drawer: '🗄️',
    shelf: '📚',
    wardrobe: '👕',
    box: '📦',
    default: '📁'
  }
  return icons[category] || icons.default
}