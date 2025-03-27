<template>
  <div class="media">
    <h1>RAZ父文件列表</h1>
    
    <div class="filter-section">
      <!-- 搜索框 -->
      <div class="search-box">
        <input 
          v-model="searchKeyword"
          type="text"
          placeholder="输入文件名搜索..."
          @input="handleSearch"
        >
      </div>

      <!-- 字母过滤器 -->
      <div class="letter-filter">
        <span 
          v-for="letter in ['All', ...letters]" 
          :key="letter"
          :class="{ active: currentLetter === letter }"
          @click="filterByLetter(letter)">
          {{ letter }}
        </span>
      </div>
      
      <!-- 字母过滤器下的分页 -->
      <div class="pagination letter-pagination">
        <span>共 {{ totalElements }} 条记录</span>
        <button 
          :disabled="currentPage === 0"
          @click="changePage(currentPage - 1)">
          上一页
        </button>
        <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
        <button 
          :disabled="currentPage >= totalPages - 1"
          @click="changePage(currentPage + 1)">
          下一页
        </button>
      </div>
    </div>

    <!-- 移除 action-bar -->

    <div class="media-list">
      <div class="media-item" v-for="file in fileList" :key="file.id" @click="goToDetail(file)">
        <div class="media-content">
          <div class="cover-image">
            <img :src="file.coverPictureUrl" :alt="file.razParentFileName">
            <!-- 添加播放按钮 -->
            <div v-if="file.mp3Url" class="play-button" @click.stop="playAudio(file)">
              <span v-if="currentPlayingFile?.id !== file.id">▶</span>
              <span v-else>⏸</span>
            </div>
          </div>
          <h3>Level: {{ file.razLevel }}</h3>
          <p>父文件名: {{ file.razParentFileName }}</p>
          <p>文件目录: {{ file.razFileDir }}</p>
          <p class="time-info">更新时间: {{ formatDateTime(file.updateTime) }}</p>
        </div>
      </div>
    </div>
    
    <!-- 添加分页信息 -->
    <div v-if="totalPages > 1" class="pagination">
      <span>共 {{ totalElements }} 条记录</span>
      <button 
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)">
        上一页
      </button>
      <span>{{ currentPage + 1 }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage >= totalPages - 1"
        @click="changePage(currentPage + 1)">
        下一页
      </button>
    </div>
  </div>
  <!-- 添加音频播放器 -->
  <audio ref="audioPlayer" @ended="handleAudioEnd"></audio>
</template>

<script setup>
// 修复重复导入问题
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const letters = ref('ABCDEFGHIJKLMNOPQRSTUVWXYZ'.split(''))
const currentLetter = ref('')
const fileList = ref([])

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString()
}

const currentPage = ref(0)
const totalPages = ref(0)

const totalElements = ref(0)
const pageSize = 20  // 修改为 20

const searchKeyword = ref('')
let searchTimer = null

const handleSearch = () => {
  if (searchTimer) {
    clearTimeout(searchTimer)
  }
  searchTimer = setTimeout(() => {
    currentPage.value = 0
    fetchFiles()
  }, 300)
}

// 移除这些未使用的变量和方法，它们似乎是从其他组件复制过来的
// const currentIndex = ref(0)
// const currentFile = computed(() => fileList.value[currentIndex.value])
// const prevItem = () => {
//   if (currentIndex.value > 0) {
//     currentIndex.value--
//   }
// }
// const nextItem = () => {
//   if (currentIndex.value < fileList.value.length - 1) {
//     currentIndex.value++
//   }
// }

// 修改 fetchFiles 方法，重置 currentIndex
const fetchFiles = async () => {
  try {
    const params = {
      page: currentPage.value,
      size: pageSize
    }
    if (currentLetter.value && currentLetter.value !== 'All') {
      params.level = currentLetter.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    const response = await axios.get('http://localhost:8080/api/parent-files/filter', { params })
    fileList.value = response.data.content
    totalPages.value = response.data.totalPages
    totalElements.value = response.data.totalElements
    // 移除这一行，因为 currentIndex 已经不存在了
    // currentIndex.value = 0
  } catch (error) {
    console.error('Error fetching files:', error)
  }
}

const filterByLetter = async (letter) => {
  currentLetter.value = currentLetter.value === letter ? '' : letter
  currentPage.value = 0 // 重置页码
  await fetchFiles()
}

const changePage = async (page) => {
  currentPage.value = page
  await fetchFiles()
}

const goToDetail = (file) => {
  router.push({
    path: `/media/${file.id}`,
    query: {
      level: file.razLevel,
      parentFileName: file.razParentFileName,
      mp3Url: file.mp3Url  // 添加 mp3Url
    }
  })
}

const audioPlayer = ref(null)
const currentPlayingFile = ref(null)

const playAudio = (file) => {
  if (currentPlayingFile.value?.id === file.id) {
    // 如果点击的是当前正在播放的文件，则暂停
    audioPlayer.value.pause()
    currentPlayingFile.value = null
  } else {
    // 播放新的文件
    audioPlayer.value.src = file.mp3Url
    audioPlayer.value.play()
    currentPlayingFile.value = file
  }
}

const handleAudioEnd = () => {
  currentPlayingFile.value = null
}

</script>

<style scoped>
.media {
  padding: 80px 20px 20px;
  min-height: 100vh;
  background-color: #f5f5f5;
}


.media-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.cover-image {
  position: relative;
  width: 100%;
  height: 200px;
  margin-bottom: 15px;
  overflow: hidden;
  border-radius: 4px;
}

.play-button {
  position: absolute;
  bottom: 10px;
  right: 10px;
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.7);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  font-size: 20px;
  transition: all 0.3s;
}

.play-button:hover {
  background: rgba(0, 0, 0, 0.9);
  transform: scale(1.1);
}

.cover-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.media-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
  cursor: pointer;
  overflow: hidden;
}

.media-item:hover {
  transform: translateY(-5px);
}

.media-content {
  padding: 20px;
}

h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

p {
  color: #666;
  margin: 5px 0;
}

.time-info {
  margin-top: 10px;
  font-size: 0.9em;
  color: #999;
}

.filter-section {
  max-width: 1200px;
  margin: 0 auto 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.letter-filter {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  justify-content: center;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
  margin-bottom: 15px;
}

.letter-pagination {
  margin: 0;
  padding-top: 15px;
}

.letter-filter span {
  padding: 5px 10px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.letter-filter span:hover {
  background-color: #e9ecef;
}

.letter-filter span.active {
  background-color: #4CAF50;
  color: white;
}

.pagination {
  max-width: 1200px;
  margin: 20px auto;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.pagination button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  background-color: #4CAF50;
  color: white;
  cursor: pointer;
}

.pagination button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.search-box {
  margin-bottom: 20px;
  padding: 0 20px;
}

.search-box input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.2s;
}

.search-box input:focus {
  border-color: #4CAF50;
}
/* ... 其他样式保持不变 ... */

/* 移除未使用的样式 */
/* .action-bar {
  max-width: 1200px;
  margin: 0 auto 20px;
}

.add-button {
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
} */

</style>