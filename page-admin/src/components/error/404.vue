<template>
    <div class="not-found-container">
      <div class="stars">
        <div v-for="(star, index) in stars" :key="index" class="star" :style="star.style"></div>
      </div>
      
      <div class="content">
        <div class="glitch-container">
          <div class="glitch" data-text="404">404</div>
          <div class="glitch-effect"></div>
        </div>
        
        <h2 class="subtitle">页面迷失在数字宇宙中</h2>
        
        <p class="description">
          我们尝试寻找你需要的页面，但它似乎已经离开了原来的轨道<br>
          你可以返回主页，或者探索我们的其他内容
        </p>
        
        <el-button 
          type="primary" 
          class="home-button"
          @click="goHome"
        >
          <el-icon><HomeFilled /></el-icon>
          返回主页
        </el-button>
      </div>
      
      <div class="astronaut">
        <div class="helmet"></div>
        <div class="body"></div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { HomeFilled } from '@element-plus/icons-vue'
  import { useRouter } from 'vue-router'
  
  const router = useRouter()
  
  // 生成随机星星
  const stars = Array.from({ length: 100 }).map(() => ({
    style: {
      left: `${Math.random() * 100}%`,
      top: `${Math.random() * 100}%`,
      animationDelay: `${Math.random() * 3}s`,
      width: `${Math.random() * 3 + 2}px`,
      height: `${Math.random() * 3 + 2}px`
    }
  }))
  
  const goHome = () => {
    router.push('/')
  }
  </script>
  
  <style scoped>
  .not-found-container {
    min-height: 100vh;
    background: linear-gradient(45deg, #1a1a2e, #16213e);
    position: relative;
    overflow: hidden;
  }
  
  .content {
    position: relative;
    z-index: 2;
    text-align: center;
    padding: 2rem;
    color: white;
  }
  
  /* 星空效果 */
  .stars {
    position: absolute;
    width: 100%;
    height: 100%;
  }
  
  .star {
    position: absolute;
    background: white;
    border-radius: 50%;
    animation: twinkle 3s infinite;
  }
  
  @keyframes twinkle {
    0%, 100% { opacity: 0.3; }
    50% { opacity: 1; }
  }
  
  /* 故障文字效果 */
  .glitch-container {
    position: relative;
    margin: 2rem 0;
  }
  
  .glitch {
    font-size: 12rem;
    font-weight: 800;
    text-shadow: 0.05em 0 0 rgba(255,0,0,.75),
                -0.025em -0.05em 0 rgba(0,255,0,.75),
                0.025em 0.05em 0 rgba(0,0,255,.75);
    animation: glitch 500ms infinite;
  }
  
  .glitch-effect {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(transparent 60%, rgba(0,255,255,.1) 60%),
                linear-gradient(rgba(255,0,0,.1) 50%, transparent 50%);
    mix-blend-mode: overlay;
  }
  
  @keyframes glitch {
    0% { transform: translate(0); }
    20% { transform: translate(-0.05em, 0.05em); }
    40% { transform: translate(0.05em, -0.05em); }
    60% { transform: translate(-0.05em, 0.05em); }
    80% { transform: translate(0.05em, -0.05em); }
    100% { transform: translate(0); }
  }
  
  .subtitle {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
    color: #e6e6e6;
  }
  
  .description {
    font-size: 1.1rem;
    line-height: 1.6;
    color: #a0a0a0;
    max-width: 600px;
    margin: 0 auto 2rem;
  }
  
  .home-button {
    padding: 1rem 2rem;
    font-size: 1.1rem;
    border-radius: 30px;
    transition: all 0.3s;
    background: linear-gradient(45deg, #6c5ce7, #a66efa);
    border: none;
  }
  
  .home-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(108,92,231,0.3);
  }
  
  /* 宇航员动画 */
  .astronaut {
    position: absolute;
    right: 10%;
    bottom: 20%;
    width: 150px;
    height: 200px;
    animation: float 6s ease-in-out infinite;
  }
  
  @keyframes float {
    0%, 100% { transform: translateY(0); }
    50% { transform: translateY(-20px); }
  }
  
  /* 宇航员样式细节（简化的CSS绘制） */
  .helmet {
    position: absolute;
    width: 100px;
    height: 100px;
    background: #f0f0f0;
    border-radius: 50%;
    left: 25px;
    box-shadow: inset 0 0 15px rgba(0,0,0,0.1);
  }
  
  .body {
    position: absolute;
    width: 80px;
    height: 120px;
    background: #fff;
    border-radius: 40px;
    top: 80px;
    left: 35px;
  }
  </style>