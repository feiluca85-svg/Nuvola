const wp8TilesData = [
  { id: 'voti', label: 'voti', icon: '🏆', size: 'wide', keywords: ['voti', 'valutazioni'] },
  { id: 'assenze', label: 'assenze', icon: '👤', size: 'medium', keywords: ['assenze', 'presenze'] },
  { id: 'note', label: 'note', icon: '💬', size: 'medium', keywords: ['note', 'annotazioni'] },
  { id: 'compiti', label: 'compiti', icon: '📝', size: 'wide', keywords: ['compiti', 'assegnati'] },
  { id: 'calendario', label: 'calendario', icon: '📅', size: 'medium', keywords: ['calendario', 'agenda'] },
  { id: 'argomenti', label: 'argomenti', icon: '📖', size: 'medium', keywords: ['argomenti', 'lezioni'] },
  { id: 'bacheche', label: 'bacheche ed eventi', icon: '📌', size: 'wide', keywords: ['bacheche', 'avvisi', 'eventi', 'documenti ed eventi'] },
  { id: 'pagamenti', label: 'pagamenti', icon: '💳', size: 'medium', keywords: ['pagamenti', 'tasse'] },
  { id: 'modulistica', label: 'modulistica', icon: '📋', size: 'medium', keywords: ['modulistica', 'moduli'] },
  { id: 'colloqui', label: 'colloqui', icon: '🤝', size: 'medium', keywords: ['colloqui', 'ricevimento'] },
  { id: 'scrutinio', label: 'documenti scrutinio', icon: '📁', size: 'medium', keywords: ['scrutinio', 'pagelle'] },
];

function getCurrentTimeStr() {
  const now = new Date();
  const h = String(now.getHours()).padStart(2, '0');
  const m = String(now.getMinutes()).padStart(2, '0');
  return `${h}:${m}`;
}

function createWP8StartScreen() {
  if (document.getElementById('wp8-start-screen')) return;

  // 1. Create Start Screen Overlay
  const overlay = document.createElement('div');
  overlay.id = 'wp8-start-screen';

  // Header
  const header = document.createElement('div');
  header.className = 'wp8-start-header';
  
  const title = document.createElement('h1');
  title.className = 'wp8-start-title';
  title.innerText = 'start';

  const clock = document.createElement('div');
  clock.className = 'wp8-start-clock';
  clock.innerText = getCurrentTimeStr();
  setInterval(() => { clock.innerText = getCurrentTimeStr(); }, 10000);

  header.appendChild(title);
  header.appendChild(clock);
  overlay.appendChild(header);

  // Tiles Grid
  const grid = document.createElement('div');
  grid.className = 'wp8-tiles-grid';

  wp8TilesData.forEach(t => {
    const tile = document.createElement('div');
    tile.className = `wp8-start-tile wp8-tile-${t.size}`;
    
    const iconWrapper = document.createElement('div');
    iconWrapper.className = 'tile-icon-wrapper';
    const icon = document.createElement('span');
    icon.className = 'tile-icon';
    icon.innerText = t.icon;
    iconWrapper.appendChild(icon);

    const label = document.createElement('span');
    label.className = 'tile-label';
    label.innerText = t.label;

    tile.appendChild(iconWrapper);
    tile.appendChild(label);

    // Click handler
    tile.onclick = (e) => {
      e.stopPropagation();
      openNuvolaSection(t);
    };

    grid.appendChild(tile);
  });

  overlay.appendChild(grid);
  document.body.appendChild(overlay);

  // 2. Create Back Bar for subpages
  createWP8BackBar();
}

function createWP8BackBar() {
  if (document.getElementById('wp8-back-bar')) return;

  const bar = document.createElement('div');
  bar.id = 'wp8-back-bar';
  bar.style.display = 'none'; // hidden initially

  const btn = document.createElement('button');
  btn.id = 'wp8-back-btn';
  btn.innerHTML = '&#8592; start';
  btn.onclick = () => {
    showStartScreen();
  };

  bar.appendChild(btn);
  document.body.appendChild(bar);
}

function showStartScreen() {
  const startScreen = document.getElementById('wp8-start-screen');
  const backBar = document.getElementById('wp8-back-bar');
  if (startScreen) startScreen.style.display = 'block';
  if (backBar) backBar.style.display = 'none';
  document.body.classList.remove('wp8-in-subpage');
}

function hideStartScreen() {
  const startScreen = document.getElementById('wp8-start-screen');
  const backBar = document.getElementById('wp8-back-bar');
  if (startScreen) startScreen.style.display = 'none';
  if (backBar) backBar.style.display = 'flex';
  document.body.classList.add('wp8-in-subpage');
}

function openNuvolaSection(tileData) {
  // 1. Search inside Nuvola's sidebar menu specifically
  const sidebarLinks = Array.from(document.querySelectorAll('.sidebar a, .sidebar-menu a, [class*="sidebar"] a, [class*="drawer"] a, nav a'));
  
  let targetElement = null;

  for (const keyword of tileData.keywords) {
    targetElement = sidebarLinks.find(el => {
      const text = (el.innerText || '').toLowerCase();
      return text.includes(keyword) && !text.includes('home');
    });
    if (targetElement) break;
  }

  // 2. If not found in sidebar, search all links on page
  if (!targetElement) {
    const allLinks = Array.from(document.querySelectorAll('a'));
    for (const keyword of tileData.keywords) {
      targetElement = allLinks.find(el => {
        const text = (el.innerText || '').toLowerCase();
        return text.includes(keyword) && el.id !== 'wp8-back-btn';
      });
      if (targetElement) break;
    }
  }

  if (targetElement) {
    hideStartScreen();
    // Trigger authentic click event
    const clickEvent = new MouseEvent('click', {
      view: window,
      bubbles: true,
      cancelable: true
    });
    targetElement.dispatchEvent(clickEvent);
    
    if (targetElement.tagName === 'A' && targetElement.href && !targetElement.href.includes('javascript') && !targetElement.href.endsWith('#')) {
      window.location.href = targetElement.href;
    }
  } else {
    // Fallback: hide start screen to show page content
    hideStartScreen();
  }
}

function createPaletteChooser() {
  if (document.getElementById('wp8-palette-chooser')) return;

  const colors = [
    { name: 'Cyan', hex: '#1ba1e2' },
    { name: 'Magenta', hex: '#ff0097' },
    { name: 'Lime', hex: '#8cbF26' },
    { name: 'Orange', hex: '#f09609' },
    { name: 'Crimson', hex: '#a20025' },
    { name: 'Purple', hex: '#a200ff' },
    { name: 'Yellow', hex: '#e3c800' },
    { name: 'Teal', hex: '#00aba9' },
    { name: 'Emerald', hex: '#008a00' },
  ];

  const chooser = document.createElement('div');
  chooser.id = 'wp8-palette-chooser';
  
  const title = document.createElement('div');
  title.className = 'wp8-chooser-title';
  title.innerText = 'Stile WP8';
  chooser.appendChild(title);

  // Theme Toggle Button
  const themeToggle = document.createElement('button');
  themeToggle.className = 'wp8-theme-toggle';
  themeToggle.innerText = 'Sfondo: Scuro';
  themeToggle.onclick = () => {
    const html = document.documentElement;
    html.classList.toggle('wp8-light-theme');
    const isLight = html.classList.contains('wp8-light-theme');
    themeToggle.innerText = isLight ? 'Sfondo: Chiaro' : 'Sfondo: Scuro';
    chrome.storage.local.set({ 'wp8Theme': isLight ? 'light' : 'dark' });
  };
  chooser.appendChild(themeToggle);

  // Colors grid
  const grid = document.createElement('div');
  grid.className = 'wp8-color-grid';
  
  colors.forEach(c => {
    const swatch = document.createElement('div');
    swatch.className = 'wp8-color-swatch';
    swatch.style.backgroundColor = c.hex;
    swatch.title = c.name;
    swatch.onclick = () => {
      document.documentElement.style.setProperty('--wp8-accent', c.hex);
      chrome.storage.local.set({ 'wp8AccentColor': c.hex });
    };
    grid.appendChild(swatch);
  });
  
  chooser.appendChild(grid);
  document.body.appendChild(chooser);
}

function initWP8() {
  chrome.storage.local.get(['wp8AccentColor', 'wp8Theme'], (result) => {
    if (result.wp8AccentColor) {
      document.documentElement.style.setProperty('--wp8-accent', result.wp8AccentColor);
    }
    if (result.wp8Theme === 'light') {
      document.documentElement.classList.add('wp8-light-theme');
    }
  });

  createWP8StartScreen();
  createPaletteChooser();

  // If on a subpage path, hide start screen
  if (window.location.pathname.length > 15 && !window.location.pathname.endsWith('/area-tutore')) {
    hideStartScreen();
  }
}

if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', initWP8);
} else {
  initWP8();
}
