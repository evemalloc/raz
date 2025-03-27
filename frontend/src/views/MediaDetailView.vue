<template>
  <div class="media-detail">
    <div class="parent-info">
      <h2>父文件信息</h2>
      <p>Level: {{ route.query.level }}</p>
      <p>父文件名: {{ route.query.parentFileName }}</p>
    </div>

    <div class="child-files">
      <h3>子文件列表</h3>
      <!-- 添加音频播放器 -->
      <div v-if="route.query.mp3Url" class="audio-player">
        <button class="play-button" @click="togglePlay">
          <span>{{ isPlaying ? '⏸' : '▶' }}</span>
        </button>
        <div class="audio-progress">
          <div class="progress-bar">
            <div class="progress-current" :style="{ width: `${progress}%` }"></div>
          </div>
          <div class="time-display">
            <span>{{ formatTime(currentTime) }}</span>
            <span>{{ formatTime(duration) }}</span>
          </div>
        </div>
        <span class="audio-title">背景音乐</span>
      </div>
      <div class="file-navigation">
        <button class="nav-button prev" @click="prevItem" :disabled="currentIndex === 0">
          <span class="arrow">←</span>
        </button>

        <div class="file-item" v-if="currentFile">
          <div class="file-content">
            <div class="file-content">
              <h4>Level: {{ currentFile.razLevel }}</h4>
              <!-- 添加图片展示 -->
              <div class="image-container" v-if="currentFile.razChildPngFileUrl">
                <img :src="currentFile.razChildPngFileUrl" alt="图片预览" class="preview-image">
                <div class="file-counter">
                  {{ currentIndex + 1 }} / {{ fileList.length }}
                </div>
              </div>
              <!-- 移动音频播放器到这里 -->
              <div v-if="route.query.mp3Url" class="audio-player">
                <button class="play-button" @click="togglePlay">
                  <span>{{ isPlaying ? '⏸' : '▶' }}</span>
                </button>
                <div class="audio-progress">
                  <div class="progress-bar">
                    <div class="progress-current" :style="{ width: `${progress}%` }"></div>
                  </div>
                  <div class="time-display">
                    <span>{{ formatTime(currentTime) }}</span>
                    <span>{{ formatTime(duration) }}</span>
                  </div>
                </div>
                <span class="audio-title">背景音乐</span>
              </div>
              <div class="time-edit">
                <label>开始时间:</label>
                <input v-model="currentFile.razStartTime" type="text">
              </div>
              <div class="time-edit">
                <label>结束时间:</label>
                <input v-model="currentFile.razEndTime" type="text">
              </div>
              <p>文件目录: {{ currentFile.razFileDir }}</p>
              <p>子文件: {{ currentFile.razChildPngFileName }}</p>
              <!-- 添加时间区间播放按钮 -->
              <div class="time-range-player">
                <button class="play-range-button" @click="playTimeRange" :disabled="!route.query.mp3Url">
                  <span>{{ isRangePlaying ? '⏸' : '▶' }}</span>
                  播放时间区间
                </button>
              </div>
              <p class="time-info">更新时间: {{ formatDateTime(currentFile.updateTime) }}</p>
              <div class="save-button-container">
                <button class="save-button" @click="saveChanges">保存修改</button>
              </div>
            </div>
          </div>
        </div>

        <button class="nav-button next" @click="nextItem" :disabled="currentIndex >= fileList.length - 1">
          <span class="arrow">→</span>
        </button>
      </div>

      <div class="file-counter">
        {{ currentIndex + 1 }} / {{ fileList.length }}
      </div>

      <!-- 分页控件保持不变 -->
    </div>
  </div>
  <audio ref="audioPlayer" @ended="handleAudioEnd"></audio>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const fileList = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 100

const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return new Date(datetime).toLocaleString()
}

const currentIndex = ref(0)
const currentFile = computed(() => fileList.value[currentIndex.value])

const prevItem = () => {
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const nextItem = () => {
  if (currentIndex.value < fileList.value.length - 1) {
    currentIndex.value++
    const nextFile = fileList.value[currentIndex.value]
    const currentTime = audioPlayer.value ? Math.floor(audioPlayer.value.currentTime).toString() : null

    // 如果开始时间为空，设置为当前播放时间
    if (!nextFile.razStartTime && currentTime) {
      nextFile.razStartTime = currentTime
    }
    
    // 如果有开始时间但结束时间为空，则每次都更新结束时间为当前进度
    if (nextFile.razStartTime && !nextFile.razEndTime && currentTime) {
      nextFile.razStartTime = currentTime
    }
  }
}

const fetchFiles = async () => {
  try {
    const params = {
      level: route.query.level,
      parentFileName: route.query.parentFileName,
      page: currentPage.value,
      size: pageSize
    }
    const response = await axios.get('http://localhost:8080/api/files/children', { params })
    // 对文件列表进行排序
    const sortedFiles = response.data.content.sort((a, b) => {
      const pageNumA = parseInt(a.razChildPngFileName.match(/page(\d+)/i)?.[1] || 0)
      const pageNumB = parseInt(b.razChildPngFileName.match(/page(\d+)/i)?.[1] || 0)
      return pageNumA - pageNumB
    })
    fileList.value = sortedFiles
    totalPages.value = response.data.totalPages
    totalElements.value = response.data.totalElements
    currentIndex.value = 0
  } catch (error) {
    console.error('Error fetching child files:', error)
  }
}

const changePage = async (page) => {
  currentPage.value = page
  await fetchFiles()
}

const saveChanges = async () => {
  try {
    const saveData = {
      id: currentFile.value.id,
      razLevel: currentFile.value.razLevel,
      razParentFileName: currentFile.value.razParentFileName,
      razChildPngFileName: currentFile.value.razChildPngFileName,
      razFileDir: currentFile.value.razFileDir,
      razStartTime: currentFile.value.razStartTime,
      razEndTime: currentFile.value.razEndTime,
      razChildPngFileUrl: currentFile.value.razChildPngFileUrl  // 添加这一行
    }
    
    // 使用 PUT 请求，并确保 Content-Type 设置正确
    await axios.put(`http://localhost:8080/api/files/${currentFile.value.id}`, saveData, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    alert('保存成功')
  } catch (error) {
    console.error('Error saving changes:', error)
    alert('保存失败：' + (error.response?.data?.message || error.message))
  }
}

onMounted(() => {
  fetchFiles()
})

const audioPlayer = ref(null)
const isPlaying = ref(false)

const togglePlay = () => {
  if (!audioPlayer.value.src) {
    audioPlayer.value.src = route.query.mp3Url
  }
  
  if (isPlaying.value) {
    audioPlayer.value.pause()
  } else {
    audioPlayer.value.play()
  }
  isPlaying.value = !isPlaying.value
}

const isRangePlaying = ref(false)

const playTimeRange = () => {
  if (!audioPlayer.value.src) {
    audioPlayer.value.src = route.query.mp3Url
  }

  if (isRangePlaying.value) {
    audioPlayer.value.pause()
    isRangePlaying.value = false
    return
  }

  // 每次播放时获取最新的时间值
  const startTime = parseFloat(currentFile.value.razStartTime)
  const endTime = parseFloat(currentFile.value.razEndTime)
  
  if (isNaN(startTime) || isNaN(endTime)) {
    alert('请输入正确的秒数')
    return
  }

  // 设置音频开始时间
  audioPlayer.value.currentTime = startTime
  audioPlayer.value.play()
  isRangePlaying.value = true

  // 监听时间更新
  const timeUpdateHandler = () => {
    if (audioPlayer.value.currentTime >= endTime) {
      audioPlayer.value.pause()
      isRangePlaying.value = false
      audioPlayer.value.removeEventListener('timeupdate', timeUpdateHandler)
    }
  }

  // 移除之前的监听器（如果有）并添加新的监听器
  audioPlayer.value.removeEventListener('timeupdate', timeUpdateHandler)
  audioPlayer.value.addEventListener('timeupdate', timeUpdateHandler)
}

// 将时间字符串转换为秒数
const parseTimeToSeconds = (timeStr) => {
  if (!timeStr) return null
  const parts = timeStr.split(':')
  if (parts.length !== 2) return null
  
  const minutes = parseInt(parts[0])
  const seconds = parseInt(parts[1])
  
  if (isNaN(minutes) || isNaN(seconds)) return null
  
  return minutes * 60 + seconds
}

// 修改原有的音频结束处理
const handleAudioEnd = () => {
  isPlaying.value = false
  isRangePlaying.value = false
}

const currentTime = ref(0)
const duration = ref(0)
const progress = ref(0)

// 修改格式化时间显示的函数
const formatTime = (time) => {
  if (!time) return '0.000'
  return time.toFixed(3)
}

// 更新进度计算方法
const updateProgress = () => {
  if (audioPlayer.value) {
    currentTime.value = audioPlayer.value.currentTime
    duration.value = audioPlayer.value.duration
    // 保留3位小数的进度百分比
    progress.value = ((currentTime.value / duration.value) * 100).toFixed(3)
  }
}

onMounted(() => {
  fetchFiles()
  // 添加音频事件监听
  if (audioPlayer.value) {
    audioPlayer.value.addEventListener('timeupdate', updateProgress)
    audioPlayer.value.addEventListener('loadedmetadata', updateProgress)
  }
})

</script>

<style scoped>
.media-detail {
  padding: 80px 20px 20px;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  color: #666;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button[type="submit"] {
  background-color: #4CAF50;
  color: white;
}

.delete {
  background-color: #f44336;
  color: white;
}

.file-navigation {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin: 20px 0;
}

.nav-button {
  background: rgba(0, 0, 0, 0.5);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.nav-button:not(:disabled):hover {
  background: rgba(0, 0, 0, 0.7);
}

.arrow {
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.file-item {
  flex: 1;
  max-width: 600px;
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.file-counter {
  text-align: center;
  margin-top: 10px;
  color: #666;
  font-size: 14px;
}

.file-content h4 {
  margin-bottom: 15px;
  color: #2c3e50;
}

.file-content p {
  margin: 10px 0;
  color: #666;
}

.time-edit {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.time-edit label {
  min-width: 80px;
  color: #666;
}

.time-edit input {
  flex: 1;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.save-button-container {
  margin-top: 20px;
  text-align: right;
}

.save-button {
  background-color: #4CAF50;
  color: white;
  padding: 8px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.save-button:hover {
  background-color: #45a049;
}

.audio-player {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  width: 100%;
}

.play-button {
  width: 40px;
  height: 40px;
  background: rgba(0, 0, 0, 0.7);
  border: none;
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

.audio-title {
  color: #666;
  font-size: 14px;
}
.time-range-player {
  margin: 15px 0;
  display: flex;
  justify-content: center;
}

.play-range-button {
  display: flex;
  align-items: center;
  gap: 8px;
  background-color: #2196F3;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.play-range-button:hover {
  background-color: #1976D2;
}

.play-range-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
.audio-progress {
  flex: 1;
  margin: 0 15px;
}

.progress-bar {
  width: 100%;
  height: 4px;
  background: #ddd;
  border-radius: 2px;
  overflow: hidden;
  cursor: pointer;
}

.progress-current {
  height: 100%;
  background: #2196F3;
  transition: width 0.1s linear;
}

.time-display {
  display: flex;
  justify-content: space-between;
  margin-top: 5px;
  font-size: 12px;
  color: #666;
}

.audio-player {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 8px;
}
.image-container {
  margin: 15px 0;
  text-align: center;
}

.preview-image {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>