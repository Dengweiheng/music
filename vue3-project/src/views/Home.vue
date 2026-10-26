<template>
  <div class="music-app">
    <!-- 侧边栏 -->
    <aside class="sidebar">
      <div class="logo-section">
        <el-icon :size="28" color="#333"><Headset /></el-icon>
        <span class="logo-text">MusicBox</span>
      </div>
      
      <nav class="nav-menu">
        <div 
          class="nav-item" 
          :class="{ active: viewMode === 'all' }" 
          @click="changeView('all')"
        >
          <el-icon><Menu /></el-icon>
          <span>首页浏览</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: viewMode === 'categories' }" 
          @click="changeView('categories')"
        >
          <el-icon><Grid /></el-icon>
          <span>分类探索</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: viewMode === 'recent' }" 
          @click="changeView('recent')"
        >
          <el-icon><Timer /></el-icon>
          <span>最近播放</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: viewMode === 'fav' }" 
          @click="changeView('fav')"
        >
          <el-icon><Star /></el-icon>
          <span>我的收藏</span>
        </div>
        <div 
          class="nav-item" 
          :class="{ active: viewMode === 'upload' }" 
          @click="handleUserUploadClick"
        >
          <el-icon><Upload /></el-icon>
          <span>上传音乐</span>
        </div>
      </nav>

      <div class="user-card" v-if="userStore.token">
        <el-avatar :size="36" :src="userStore.userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
        <div class="user-meta">
          <span class="name">{{ userStore.userInfo.nickname || userStore.userInfo.username }}</span>
          <span class="role">{{ userStore.isAdmin() ? '管理员' : '普通用户' }}</span>
        </div>
        <el-dropdown trigger="click">
          <el-icon class="more-icon"><MoreFilled /></el-icon>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="userStore.isAdmin()" @click="router.push('/admin/music')">后台管理</el-dropdown-item>
              <el-dropdown-item @click="$router.push('/profile')">个人信息管理</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div v-else class="auth-buttons">
        <el-button class="login-btn" @click="$router.push('/login')">登录账号</el-button>
      </div>
    </aside>

    <!-- 主内容区 -->
    <main class="main-content">
      <header class="top-nav">
        <div class="search-bar">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索歌名、歌手、专辑..."
            prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </div>
      </header>

      <div class="content-scroll">
        <!-- 动态 Carousel (仅首页显示) -->
        <section class="hero-section" v-if="viewMode === 'all' && banners.length > 0">
           <el-carousel 
             height="300px" 
             border-radius="28px" 
             class="main-carousel"
             arrow="always"
             :interval="4000"
             :autoplay="true"
           >
              <el-carousel-item v-for="b in banners" :key="b.id">
                 <div class="hero-card">
                    <div class="hero-info">
                       <span class="label">精彩推荐</span>
                       <h1>{{ b.title }}</h1>
                       <p>发现更多优质音乐内容，开启你的专属音乐之旅。</p>
                    </div>
                    <img :src="b.imageUrl" class="hero-img">
                 </div>
              </el-carousel-item>
           </el-carousel>
         </section>

        <!-- 分类选择 -->
        <section class="content-block" v-if="viewMode === 'categories' || (viewMode === 'all' && styles.length > 0)">
          <div class="block-header">
            <h3>风格分类</h3>
          </div>
          <div class="genre-list">
            <div 
              class="genre-item"
              :class="{ active: selectedStyle === '' }"
              @click="handleStyleClick('')"
            >
              全部
            </div>
            <div 
              v-for="style in styles" 
              :key="style" 
              class="genre-item"
              :class="{ active: selectedStyle === style }"
              @click="handleStyleClick(style)"
            >
              {{ style }}
            </div>
          </div>
        </section>

        <!-- 个性化推荐 -->
        <section class="content-block" v-if="viewMode === 'all' && recommendations.length > 0">
          <div class="block-header">
            <div class="header-with-tag">
              <h3>猜你喜欢</h3>
              <el-tag size="small" type="danger" effect="plain" round>AI 推荐</el-tag>
            </div>
          </div>
          <div class="music-grid-modern">
            <div 
              class="music-item" 
              v-for="music in recommendations" 
              :key="'rec-' + music.id"
              @click="playMusic(music)"
            >
              <div class="cover-wrapper">
                <img :src="music.coverUrl || defaultCover" class="cover-img">
                <div class="play-hint">
                  <el-icon class="hint-icon"><VideoPlay /></el-icon>
                </div>
              </div>
              <div class="item-details">
                <p class="title">{{ music.title }}</p>
                <p class="artist">{{ music.singer }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 音乐列表网格 -->
        <section class="content-block">
          <div class="block-header">
            <h3>{{ viewTitle }}</h3>
          </div>
          <div class="music-grid-modern" v-loading="loading">
            <div 
              class="music-item" 
              v-for="music in filteredMusicList" 
              :key="music.id"
              @click="playMusic(music)"
            >
              <div class="cover-wrapper">
                <img :src="music.coverUrl || defaultCover" class="cover-img">
                <div class="play-hint">
                  <el-icon class="hint-icon" @click.stop="playMusic(music)"><VideoPlay /></el-icon>
                  <div class="hint-actions">
                    <el-icon class="sub-icon" @click.stop="addToQueue(music)"><Plus /></el-icon>
                    <el-icon class="sub-icon" @click.stop="downloadMusic(music)"><Download /></el-icon>
                  </div>
                </div>
              </div>
              <div class="item-details">
                <p class="title">{{ music.title }}</p>
                <p class="artist">{{ music.singer }} - {{ music.album || '未知专辑' }}</p>
              </div>
            </div>
          </div>
          <el-empty v-if="filteredMusicList.length === 0 && !loading" :description="emptyDescription" />
        </section>

        <div class="bottom-padding"></div>
      </div>
    </main>

    <!-- 底部悬浮播放器 -->
    <footer class="player-container" v-if="currentMusic">
      <div class="track-area">
        <img :src="currentMusic.coverUrl || defaultCover" class="thumb">
        <div class="track-meta">
          <span class="track-name">{{ currentMusic.title }}</span>
          <span class="track-artist">{{ currentMusic.singer }}</span>
        </div>
        <el-icon 
          class="heart-btn" 
          :class="{ active: isFavorited }" 
          @click="toggleFavorite"
        >
          <StarFilled v-if="isFavorited" />
          <Star v-else />
        </el-icon>
      </div>

      <div class="control-area">
        <div class="buttons">
          <el-icon class="btn-sub"><RefreshRight /></el-icon>
          <el-icon class="btn-main" @click="playPrev"><DArrowLeft /></el-icon>
          <div class="play-btn" @click="togglePlayback">
            <el-icon :size="24" v-if="isPlaying"><VideoPause /></el-icon>
            <el-icon :size="24" v-else><VideoPlay /></el-icon>
          </div>
          <el-icon class="btn-main" @click="playNext"><DArrowRight /></el-icon>
          <el-icon class="btn-sub" @click="playQueueVisible = true"><Operation /></el-icon>
        </div>
        <div class="slider-wrapper">
          <span class="time-label">{{ formatTime(currentTime) }}</span>
          <el-slider 
            v-model="currentTime" 
            :max="duration" 
            :show-tooltip="false" 
            @change="handleProgressChange"
          />
          <span class="time-label">{{ formatTime(duration) }}</span>
        </div>
      </div>

      <div class="utility-area">
        <el-icon class="util-btn" title="点击下载" @click="downloadMusic(currentMusic)"><Download /></el-icon>
        <el-icon class="util-btn" title="评论" @click="showComments(currentMusic.id)"><ChatLineRound /></el-icon>
        <el-icon class="util-btn" title="播放列表" @click="playQueueVisible = true"><Finished /></el-icon>
        <div class="vol-slider">
          <el-icon><Microphone /></el-icon>
          <el-slider v-model="volume" :max="1" :step="0.01" />
        </div>
      </div>

      <audio 
        ref="audioRef" 
        :src="currentMusic.fileUrl" 
        @timeupdate="onTimeUpdate" 
        @loadedmetadata="onLoadedMetadata"
        @ended="onEnded"
      ></audio>
    </footer>

    <!-- 播放队列抽屉 -->
    <el-drawer v-model="playQueueVisible" title="播放队列" size="400px">
      <div class="queue-header">
        <span class="count">共 {{ playQueue.length }} 首</span>
        <el-button link type="danger" @click="clearQueue">清空列表</el-button>
      </div>
      <div class="queue-list">
        <div 
          v-for="(item, index) in playQueue" 
          :key="item.id" 
          class="queue-item"
          :class="{ 'is-playing': currentMusic && currentMusic.id === item.id }"
          @dblclick="playMusic(item)"
        >
          <span class="queue-index">{{ index + 1 }}</span>
          <div class="queue-meta">
            <span class="q-title">{{ item.title }}</span>
            <span class="q-artist">{{ item.singer }}</span>
          </div>
          <el-icon class="q-del" @click.stop="removeFromQueue(item.id)"><Close /></el-icon>
        </div>
        <el-empty v-if="playQueue.length === 0" description="播放列表为空哟" />
      </div>
    </el-drawer>

    <!-- 评论抽屉 -->
    <el-drawer v-model="drawerVisible" title="歌曲评论" size="450px">
      <div class="drawer-content">
        <div class="comment-scroller" v-loading="commentLoading">
          <div class="fancy-comment" v-for="c in comments" :key="c.comment.id">
            <el-avatar :size="40" :src="c.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div class="comment-body">
              <div class="body-top">
                <span class="author">{{ c.nickname || '用户' }}</span>
                <span class="date">{{ formatDate(c.comment.createTime) }}</span>
              </div>
              <p class="content">{{ c.comment.content }}</p>
            </div>
          </div>
        </div>
        <div class="post-box">
          <el-input v-model="newComment" placeholder="分享你的听歌感悟..." type="textarea" :rows="2" />
          <el-button type="primary" class="post-btn" @click="submitComment">发送评论</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 用户上传音乐对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="我要投稿" width="550px">
      <el-form :model="uploadForm" label-position="top">
        <el-form-item label="歌曲名称">
          <el-input v-model="uploadForm.title" placeholder="请输入歌曲名称" />
        </el-form-item>
        <el-form-item label="演唱歌手">
          <el-input v-model="uploadForm.singer" placeholder="请输入歌手名称" />
        </el-form-item>
        <el-form-item label="音乐风格">
          <el-select v-model="uploadForm.style" placeholder="请选择风格" style="width: 100%">
            <el-option v-for="s in styles" :key="s" :label="s" :value="s" />
          </el-select>
        </el-form-item>
        <el-form-item label="音频文件">
          <el-upload
            action="/api/music/upload"
            :headers="uploadHeaders"
            :on-success="handleUploadFileSuccess"
            :limit="1"
          >
            <el-button type="info" plain size="small">选择音频文件</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="海报/封面">
          <el-upload
            action="/api/music/upload"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleUploadCoverSuccess"
          >
            <img v-if="uploadForm.coverUrl" :src="uploadForm.coverUrl" class="upload-preview" />
            <el-button v-else type="info" plain size="small">上传封面图</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitUserUpload">提交审核</el-button>
      </template>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog v-model="noticeDialogVisible" :title="selectedNotice?.title" width="500px">
      <div class="notice-detail-content">
        <p>{{ selectedNotice?.content }}</p>
        <div class="notice-meta">
           <span>发布时间: {{ formatLongDate(selectedNotice?.createTime) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { useUserStore } from '@/store/user'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api'

const userStore = useUserStore()
const router = useRouter()
const defaultCover = 'https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80&w=500&auto=format&fit=crop'

const loading = ref(false)
const musicList = ref([])
const styles = ref([])
const banners = ref([])
const notices = ref([])
const searchKeyword = ref('')
const selectedStyle = ref('')
const recommendations = ref([])
const viewMode = ref('all')
const recentPlays = ref(JSON.parse(localStorage.getItem('recentPlays') || '[]'))

// User upload state
const uploadDialogVisible = ref(false)
const uploadForm = reactive({ title: '', singer: '', style: '', fileUrl: '', coverUrl: '', userId: userStore.userInfo?.id })
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))

// Notice state
const noticeDialogVisible = ref(false)
const selectedNotice = ref(null)

// Queue state
const playQueueVisible = ref(false)
const playQueue = ref(JSON.parse(localStorage.getItem('playQueue') || '[]'))

const filteredMusicList = computed(() => {
  if (viewMode.value === 'recent') return recentPlays.value
  return musicList.value
})

const viewTitle = computed(() => {
  if (viewMode.value === 'all') return '为您推荐'
  if (viewMode.value === 'fav') return '我的收藏'
  if (viewMode.value === 'categories') return '探索结果'
  if (viewMode.value === 'recent') return '播放历史'
  return ''
})

const emptyDescription = computed(() => {
  if (viewMode.value === 'recent') return '暂无播放记录'
  if (viewMode.value === 'fav') return '您还没有收藏任何歌曲'
  return '未找到相关音乐'
})

const audioRef = ref(null)
const currentMusic = ref(null)
const isPlaying = ref(false)
const currentTime = ref(0)
const duration = ref(0)
const volume = ref(0.7)
const isFavorited = ref(false)

const drawerVisible = ref(false)
const comments = ref([])
const commentLoading = ref(false)
const newComment = ref('')

watch(volume, (val) => {
  if (audioRef.value) audioRef.value.volume = val
})

const changeView = (mode) => {
  if ((mode === 'fav' || mode === 'recent') && !userStore.token) {
    ElMessage.warning('登录后可查看更多个人动态')
    router.push('/login')
    return
  }
  viewMode.value = mode
  if (mode !== 'recent') loadData()
}

const loadData = async () => {
  loading.value = true
  try {
    if (viewMode.value === 'fav') {
      const res = await request.get(`/favorite/list/${userStore.userInfo.id}`)
      musicList.value = res
    } else {
      const res = await request.get('/music/list', {
        params: { keyword: searchKeyword.value, style: selectedStyle.value, status: 1, current: 1, size: 50 }
      })
      musicList.value = res.records
    }
  } catch (e) {
    console.error(e)
  } finally { loading.value = false }
}

const loadSystemData = async () => {
  try {
    const [styleRes, bannerRes, noticeRes] = await Promise.all([
      request.get('/music/styles'),
      request.get('/banner/list'),
      request.get('/notice/list')
    ])
    styles.value = styleRes
    banners.value = bannerRes
    notices.value = noticeRes
  } catch (e) {}
}

const loadRecommendations = async () => {
  try {
    const res = await request.get('/music/recommend', {
      params: { userId: userStore.token ? userStore.userInfo.id : null, limit: 6 }
    })
    recommendations.value = res
  } catch (e) {}
}

const handleLogout = () => {
  userStore.clearToken()
  router.push('/login')
}

const handleStyleClick = (s) => {
  selectedStyle.value = selectedStyle.value === s ? '' : s
  loadData()
}

const handleSearch = () => loadData()

const playMusic = (music) => {
  currentMusic.value = music
  isPlaying.value = true
  checkFavorite()
  addToQueue(music, true)

  const exists = recentPlays.value.findIndex(m => m.id === music.id)
  if (exists !== -1) recentPlays.value.splice(exists, 1)
  recentPlays.value.unshift(music)
  if (recentPlays.value.length > 20) recentPlays.value.pop()
  localStorage.setItem('recentPlays', JSON.stringify(recentPlays.value))

  request.post(`/music/play/${music.id}`, null, {
    params: { userId: userStore.token ? userStore.userInfo.id : null }
  }).catch(() => {})
}

const addToQueue = (music, playingNow = false) => {
  const exists = playQueue.value.findIndex(m => m.id === music.id)
  if (exists === -1) {
    playQueue.value.push(music)
    localStorage.setItem('playQueue', JSON.stringify(playQueue.value))
  }
  if (!playingNow) {
    ElMessage.success('已添加到播放队列')
    if (!currentMusic.value) playMusic(music)
  }
}

const clearQueue = () => {
  playQueue.value = []
  localStorage.removeItem('playQueue')
  currentMusic.value = null
  isPlaying.value = false
  if (audioRef.value) audioRef.value.pause()
}

const removeFromQueue = (id) => {
  playQueue.value = playQueue.value.filter(m => m.id !== id)
  localStorage.setItem('playQueue', JSON.stringify(playQueue.value))
}

const playNext = () => {
  if (playQueue.value.length === 0) return
  const currIdx = currentMusic.value ? playQueue.value.findIndex(m => m.id === currentMusic.value.id) : -1
  const nextIdx = (currIdx + 1) % playQueue.value.length
  playMusic(playQueue.value[nextIdx])
}

const playPrev = () => {
  if (playQueue.value.length === 0) return
  const currIdx = currentMusic.value ? playQueue.value.findIndex(m => m.id === currentMusic.value.id) : 0
  const prevIdx = (currIdx - 1 + playQueue.value.length) % playQueue.value.length
  playMusic(playQueue.value[prevIdx])
}

const togglePlayback = () => {
  if (!audioRef.value) return
  isPlaying.value ? audioRef.value.pause() : audioRef.value.play()
  isPlaying.value = !isPlaying.value
}

const onTimeUpdate = () => { if (audioRef.value) currentTime.value = audioRef.value.currentTime }
const onLoadedMetadata = () => { if (audioRef.value) duration.value = audioRef.value.duration }
const onEnded = () => { playNext() }
const handleProgressChange = (v) => { if (audioRef.value) audioRef.value.currentTime = v }

const showNoticeDetail = (n) => {
  selectedNotice.value = n
  noticeDialogVisible.value = true
}

const handleBannerClick = (b) => {
  if (b.linkUrl) {
    if (b.linkUrl.startsWith('http')) window.open(b.linkUrl)
    else router.push(b.linkUrl)
  }
}

const formatTime = (s) => {
  const m = Math.floor(s / 60)
  const sc = Math.floor(s % 60)
  return `${m}:${sc.toString().padStart(2, '0')}`
}

const formatDate = (ds) => {
  const d = new Date(ds)
  return `${d.getMonth()+1}/${d.getDate()} ${d.getHours()}:${d.getMinutes().toString().padStart(2, '0')}`
}

const formatLongDate = (ds) => {
  if(!ds) return '-'
  const d = new Date(ds)
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日`
}

const checkFavorite = async () => {
  if (!userStore.token || !currentMusic.value) return
  try {
    const res = await request.get('/favorite/check', {
      params: { userId: userStore.userInfo.id, musicId: currentMusic.value.id }
    })
    isFavorited.value = res
  } catch (e) {}
}

const toggleFavorite = async (e) => {
  e.stopPropagation()
  if (!userStore.token) { router.push('/login'); return }
  try {
    await request.post('/favorite/toggle', null, {
      params: { userId: userStore.userInfo.id, musicId: currentMusic.value.id }
    })
    isFavorited.value = !isFavorited.value
    if (viewMode.value === 'fav' && !isFavorited.value) loadData()
  } catch (e) {}
}

const showComments = (id) => {
  drawerVisible.value = true
  loadComments(id)
}

const loadComments = async (id) => {
  commentLoading.value = true
  try {
    const res = await request.get(`/comment/music/${id}`)
    comments.value = res
  } catch (e) {} finally { commentLoading.value = false }
}

const submitComment = async () => {
  if (!userStore.token) { router.push('/login'); return }
  if (!newComment.value.trim()) return
  try {
    await request.post('/comment/add', {
      userId: userStore.userInfo.id,
      musicId: currentMusic.value.id,
      content: newComment.value
    })
    newComment.value = ''
    loadComments(currentMusic.value.id)
  } catch (e) {}
}

const handleUserUploadClick = () => {
  if (!userStore.token) {
    ElMessage.warning('请先登录后再上传音乐')
    router.push('/login')
    return
  }
  uploadDialogVisible.value = true
}

const handleUploadFileSuccess = (res) => { if(res.code === 200) uploadForm.fileUrl = res.data }
const handleUploadCoverSuccess = (res) => { if(res.code === 200) uploadForm.coverUrl = res.data }

const submitUserUpload = async () => {
  if (!uploadForm.title || !uploadForm.fileUrl) {
     ElMessage.warning('请补全歌名和音频文件')
     return
  }
  try {
    uploadForm.userId = userStore.userInfo.id
    await request.post('/music/user/save', uploadForm)
    ElMessage.success('上传成功，请等待管理员审核')
    uploadDialogVisible.value = false
    Object.assign(uploadForm, { title: '', singer: '', style: '', fileUrl: '', coverUrl: '' })
  } catch (e) {}
}

const downloadMusic = (music) => {
  if (!music || !music.fileUrl) return
  const link = document.createElement('a')
  link.href = music.fileUrl
  link.download = `${music.title} - ${music.singer}.mp3`
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  ElMessage.success('开始下载...')
}

const toggleFavoriteMusic = async (music) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录后再进行操作')
    router.push('/login')
    return
  }
  try {
    const res = await request.post('/favorite/toggle', null, {
      params: { userId: userStore.userInfo.id, musicId: music.id }
    })
    ElMessage.success(res)
    // If we're on the current song, update the heart icon in the footer
    if (currentMusic.value && currentMusic.value.id === music.id) {
      checkFavorite()
    }
  } catch (e) {}
}

onMounted(() => { 
  loadData(); 
  loadSystemData();
  loadRecommendations();
})
</script>

<style scoped>
.music-app {
  display: flex;
  height: 100vh;
  background-color: #f5f5f7;
  color: #1d1d1f;
  --el-text-color-primary: #1d1d1f;
}

.sidebar { width: 260px; background-color: #ffffff; border-right: 1px solid #e5e5e5; display: flex; flex-direction: column; padding: 32px 20px; }
.logo-section { display: flex; align-items: center; gap: 12px; margin-bottom: 48px; padding-left: 12px; }
.logo-text { font-size: 24px; font-weight: 800; color: #1d1d1f; }
.nav-menu { flex: 1; }
.nav-item { display: flex; align-items: center; gap: 12px; padding: 12px 16px; border-radius: 12px; color: #515154; cursor: pointer; transition: all 0.2s; font-weight: 600; margin-bottom: 8px; }
.nav-item:hover, .nav-item.active { background-color: #f5f5f7; color: #1d1d1f; }
.user-card { display: flex; align-items: center; gap: 12px; background: #f5f5f7; padding: 12px; border-radius: 16px; }
.user-meta { flex: 1; display: flex; flex-direction: column; }
.user-meta .name { font-size: 14px; font-weight: 700; }
.user-meta .role { font-size: 11px; color: #515154; }
.auth-buttons { padding: 12px; }
.login-btn { width: 100%; border-radius: 12px; height: 44px; font-weight: 700; }

.main-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.top-nav { height: 80px; display: flex; align-items: center; padding: 0 40px; background: rgba(245, 245, 247, 0.95); backdrop-filter: blur(20px); z-index: 100; gap: 40px; }
.search-bar { width: 350px; }
.notice-bar { flex: 1; background: #ffffff; border-radius: 12px; height: 40px; display: flex; align-items: center; padding: 0 16px; gap: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.02); overflow: hidden; }
.notice-text { font-size: 13px; color: #1d1d1f; cursor: pointer; white-space: nowrap; }

.content-scroll { flex: 1; overflow-y: auto; padding: 20px 40px; }
.hero-section { margin-bottom: 48px; }
.main-carousel { border-radius: 28px; overflow: hidden; }
.hero-card { height: 100%; width: 100%; background: #ffffff; display: flex; cursor: pointer; transition: transform 0.3s; }
.hero-card:hover { transform: translateY(-4px); }
.hero-info { flex: 1; padding: 40px; display: flex; flex-direction: column; justify-content: center; align-items: flex-start; }
.label { color: #515154; font-size: 14px; font-weight: 700; margin-bottom: 12px; }
.hero-info h1 { font-size: 32px; font-weight: 800; margin: 0 0 16px 0; color: #1d1d1f; }
.hero-info p { color: #515154; font-size: 15px; margin-bottom: 24px; }
.hero-img { width: 45%; height: 100%; object-fit: cover; }

.content-block { margin-bottom: 40px; }
.block-header h3 { font-size: 22px; font-weight: 800; color: #121212; margin-bottom: 20px; }
.header-with-tag { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.header-with-tag h3 { margin-bottom: 0; }
.genre-list { display: flex; gap: 12px; overflow-x: auto; padding-bottom: 8px; }
.genre-item { padding: 10px 24px; background: #ffffff; border-radius: 14px; font-size: 14px; font-weight: 700; cursor: pointer; box-shadow: 0 4px 12px rgba(0,0,0,0.03); white-space: nowrap; color: #1d1d1f; }
.genre-item.active { background: #1d1d1f; color: #ffffff; }

.music-grid-modern { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 32px; }
.music-item { cursor: pointer; }
.cover-wrapper { position: relative; aspect-ratio: 1; border-radius: 20px; overflow: hidden; box-shadow: 0 12px 24px rgba(0,0,0,0.08); margin-bottom: 12px; }
.cover-img { width: 100%; height: 100%; object-fit: cover; transition: transform 0.4s; }
.music-item:hover .cover-img { transform: scale(1.08); }
.play-hint { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; flex-direction: column; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.3s; color: #fff; gap: 16px; }
.music-item:hover .play-hint { opacity: 1; }
.hint-icon { font-size: 48px; cursor: pointer; transition: transform 0.2s; }
.hint-icon:hover { transform: scale(1.1); }
.hint-actions { display: flex; gap: 20px; }
.sub-icon { font-size: 20px; cursor: pointer; color: rgba(255,255,255,0.8); transition: color 0.2s; }
.sub-icon:hover { color: #ffffff; }

.title { font-weight: 700; font-size: 16px; margin: 0; color: #1d1d1f; }
.artist { font-size: 13px; color: #86868b; margin: 4px 0 0 0; }

.bottom-padding { height: 120px; }
.player-container { position: fixed; bottom: 24px; left: 300px; right: 40px; height: 84px; background: rgba(255, 255, 255, 0.9); backdrop-filter: blur(20px); border-radius: 24px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); display: flex; align-items: center; padding: 0 24px; z-index: 1000; }
.track-area { width: 25%; display: flex; align-items: center; gap: 12px; }
.thumb { width: 52px; height: 52px; border-radius: 12px; object-fit: cover; }
.track-meta { display: flex; flex-direction: column; overflow: hidden; }
.track-name { font-weight: 700; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.track-artist { font-size: 12px; color: #86868b; }
.heart-btn { font-size: 20px; cursor: pointer; color: #86868b; }
.heart-btn.active { color: #ff3b30; }

.control-area { flex: 1; display: flex; flex-direction: column; align-items: center; gap: 4px; }
.buttons { display: flex; align-items: center; gap: 24px; }
.btn-main { font-size: 24px; cursor: pointer; color: #1d1d1f; }
.btn-sub { font-size: 18px; cursor: pointer; color: #86868b; }
.play-btn { width: 44px; height: 44px; background: #1d1d1f; color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.slider-wrapper { width: 100%; max-width: 500px; display: flex; align-items: center; gap: 12px; }
.time-label { font-size: 11px; color: #86868b; min-width: 35px; }

.utility-area { width: 25%; display: flex; align-items: center; justify-content: flex-end; gap: 16px; }
.util-btn { font-size: 20px; cursor: pointer; color: #515154; }
.vol-slider { width: 100px; display: flex; align-items: center; gap: 8px; }

.notice-meta { margin-top: 16px; font-size: 12px; color: #86868b; text-align: right; }
.upload-preview { width: 100px; height: 100px; object-fit: cover; border-radius: 8px; margin-bottom: 8px; display: block; }

/* 播放队列样式 */
.queue-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; padding: 0 4px; }
.queue-header .count { font-size: 14px; font-weight: 700; color: #86868b; }
.queue-list { display: flex; flex-direction: column; gap: 4px; }
.queue-item { display: flex; align-items: center; gap: 16px; padding: 12px; border-radius: 12px; cursor: pointer; transition: all 0.2s; border: 1px solid transparent; }
.queue-item:hover { background-color: #f5f5f7; }
.queue-item.is-playing { background-color: #f0f7ff; border-color: #d0e7ff; }
.queue-item.is-playing .q-title { color: #007aff; }
.queue-index { width: 24px; font-size: 13px; color: #86868b; text-align: center; }
.queue-meta { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.q-title { font-size: 14px; font-weight: 700; color: #1d1d1f; }
.q-artist { font-size: 12px; color: #86868b; }
.q-del { font-size: 16px; color: #86868b; cursor: pointer; opacity: 0; transition: opacity 0.2s; }
.queue-item:hover .q-del { opacity: 1; }
.q-del:hover { color: #ff3b30; }

/* 评论区样式 */
.drawer-content { display: flex; flex-direction: column; height: calc(100vh - 100px); }
.comment-scroller { flex: 1; overflow-y: auto; padding-right: 8px; }
.fancy-comment { display: flex; gap: 16px; margin-bottom: 24px; padding-bottom: 20px; border-bottom: 1px solid #f5f5f7; }
.comment-body { flex: 1; }
.body-top { display: flex; justify-content: space-between; margin-bottom: 6px; }
.author { font-size: 14px; font-weight: 700; color: #1d1d1f; }
.date { font-size: 12px; color: #86868b; }
.content { font-size: 14px; color: #424245; line-height: 1.6; margin: 0; }
.post-box { margin-top: 24px; padding-top: 20px; border-top: 1px solid #e5e5e5; display: flex; flex-direction: column; gap: 12px; }
.post-btn { align-self: flex-end; border-radius: 12px; font-weight: 700; padding: 10px 24px; }

:deep(.el-drawer__body) { padding: 24px; }
:deep(.el-drawer__header) { margin-bottom: 0; padding: 24px; border-bottom: 1px solid #f5f5f7; }
:deep(.el-drawer__title) { font-weight: 800; color: #1d1d1f; }
</style>
