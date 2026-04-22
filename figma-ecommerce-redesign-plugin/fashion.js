(async function () {
  const c = {
    ink: "#111827",
    text: "#263241",
    muted: "#667085",
    faint: "#98A2B3",
    line: "#E4E7EC",
    page: "#F7F8FA",
    surface: "#FFFFFF",
    brand: "#E11D48",
    brandDark: "#9F1239",
    navy: "#172554",
    teal: "#0F766E",
    mint: "#CCFBF1",
    lavender: "#EDE9FE",
    violet: "#6D28D9",
    amber: "#F59E0B",
    orange: "#F97316",
    roseSoft: "#FFE4E6",
    blueSoft: "#DBEAFE",
    cream: "#FFF7ED",
    green: "#16A34A"
  };

  const font = await loadFontSet();

  async function loadFontSet() {
    try {
      await figma.loadFontAsync({ family: "Inter", style: "Regular" });
      await figma.loadFontAsync({ family: "Inter", style: "Medium" });
      await figma.loadFontAsync({ family: "Inter", style: "Semi Bold" });
      await figma.loadFontAsync({ family: "Inter", style: "Bold" });
      return { family: "Inter", regular: "Regular", medium: "Medium", semibold: "Semi Bold", bold: "Bold" };
    } catch (error) {
      const fonts = await figma.listAvailableFontsAsync();
      const fallback = fonts[0] ? fonts[0].fontName : { family: "Arial", style: "Regular" };
      await figma.loadFontAsync(fallback);
      return { family: fallback.family, regular: fallback.style, medium: fallback.style, semibold: fallback.style, bold: fallback.style };
    }
  }

  function rgb(hex) {
    const raw = hex.replace("#", "");
    const n = parseInt(raw.length === 3 ? raw.split("").map((v) => v + v).join("") : raw, 16);
    return { r: ((n >> 16) & 255) / 255, g: ((n >> 8) & 255) / 255, b: (n & 255) / 255 };
  }

  function paint(hex, opacity) {
    return { type: "SOLID", color: rgb(hex), opacity: opacity === undefined ? 1 : opacity };
  }

  function pageName(base) {
    const names = figma.root.children.map((page) => page.name);
    if (!names.includes(base)) return base;
    let i = 2;
    while (names.includes(base + " " + i)) i += 1;
    return base + " " + i;
  }

  function frame(parent, name, x, y, w, h, fill, r) {
    const node = figma.createFrame();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(w, h);
    node.fills = [paint(fill || c.surface)];
    node.strokes = [];
    node.clipsContent = false;
    if (r !== undefined) node.cornerRadius = r;
    return node;
  }

  function rect(parent, name, x, y, w, h, fill, r, stroke) {
    const node = figma.createRectangle();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(w, h);
    node.fills = [paint(fill || c.surface)];
    node.strokes = stroke ? [paint(stroke)] : [];
    node.strokeWeight = stroke ? 1 : 0;
    if (r !== undefined) node.cornerRadius = r;
    return node;
  }

  function ellipse(parent, name, x, y, w, h, fill, stroke) {
    const node = figma.createEllipse();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(w, h);
    node.fills = [paint(fill || c.surface)];
    node.strokes = stroke ? [paint(stroke)] : [];
    node.strokeWeight = stroke ? 1 : 0;
    return node;
  }

  function line(parent, name, x, y, w) {
    return rect(parent, name, x, y, w, 1, c.line);
  }

  function shadow(node, opacity) {
    node.effects = [{
      type: "DROP_SHADOW",
      color: { r: 17 / 255, g: 24 / 255, b: 39 / 255, a: opacity || 0.12 },
      offset: { x: 0, y: 12 },
      radius: 28,
      spread: -6,
      visible: true,
      blendMode: "NORMAL"
    }];
  }

  async function text(parent, name, value, x, y, size, weight, fill, width) {
    const node = figma.createText();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.fontName = { family: font.family, style: font[weight || "regular"] || font.regular };
    node.fontSize = size;
    node.lineHeight = { unit: "PIXELS", value: Math.round(size * 1.3) };
    node.letterSpacing = { unit: "PERCENT", value: 0 };
    node.fills = [paint(fill || c.ink)];
    node.characters = value;
    if (width) {
      node.textAutoResize = "HEIGHT";
      node.resize(width, node.height);
    } else {
      node.textAutoResize = "WIDTH_AND_HEIGHT";
    }
    return node;
  }

  async function pill(parent, name, label, x, y, bg, fg, w) {
    const p = frame(parent, name, x, y, w || 112, 34, bg, 17);
    p.layoutMode = "HORIZONTAL";
    p.primaryAxisAlignItems = "CENTER";
    p.counterAxisAlignItems = "CENTER";
    p.paddingLeft = 14;
    p.paddingRight = 14;
    const t = await text(p, name + " Text", label, 0, 0, 13, "medium", fg || c.text);
    t.layoutAlign = "CENTER";
    p.resize(w || Math.max(72, t.width + 28), 34);
    return p;
  }

  async function button(parent, name, label, x, y, w, h, variant) {
    const styles = {
      primary: [c.brand, c.surface, null],
      dark: [c.ink, c.surface, null],
      orange: [c.orange, c.surface, null],
      ghost: [c.surface, c.ink, c.line],
      soft: [c.roseSoft, c.brandDark, null],
      teal: [c.teal, c.surface, null]
    };
    const s = styles[variant || "primary"];
    const b = frame(parent, name, x, y, w, h, s[0], 8);
    b.layoutMode = "HORIZONTAL";
    b.primaryAxisAlignItems = "CENTER";
    b.counterAxisAlignItems = "CENTER";
    b.itemSpacing = 8;
    b.paddingLeft = 16;
    b.paddingRight = 16;
    if (s[2]) {
      b.strokes = [paint(s[2])];
      b.strokeWeight = 1;
    }
    const labelNode = await text(b, name + " Label", label, 0, 0, 14, "semibold", s[1]);
    labelNode.layoutAlign = "CENTER";
    return b;
  }

  function modelArt(parent, name, x, y, w, h, bg, accent, skin) {
    const a = frame(parent, name, x, y, w, h, bg, 8);
    a.clipsContent = true;
    rect(a, "Photo depth panel", w * 0.58, 0, w * 0.42, h, accent, 0);
    ellipse(a, "Face", w * 0.42, h * 0.16, w * 0.16, w * 0.16, skin || "#F6C7A7");
    rect(a, "Hair", w * 0.39, h * 0.14, w * 0.22, h * 0.08, c.ink, 8);
    rect(a, "Torso", w * 0.34, h * 0.34, w * 0.34, h * 0.38, accent, 16);
    rect(a, "Coat left", w * 0.22, h * 0.37, w * 0.22, h * 0.44, c.surface, 8);
    rect(a, "Coat right", w * 0.58, h * 0.37, w * 0.2, h * 0.44, c.surface, 8);
    rect(a, "Leg left", w * 0.4, h * 0.7, w * 0.11, h * 0.28, c.ink, 6);
    rect(a, "Leg right", w * 0.53, h * 0.7, w * 0.11, h * 0.28, c.ink, 6);
    ellipse(a, "Accent circle", w * 0.68, h * 0.1, w * 0.18, w * 0.18, c.amber);
    return a;
  }

  function productArt(parent, name, x, y, w, h, bg, accent, type) {
    const a = frame(parent, name, x, y, w, h, bg, 8);
    a.clipsContent = true;
    if (type === "shoe") {
      ellipse(a, "Shoe heel", w * 0.22, h * 0.45, w * 0.32, h * 0.22, c.surface);
      rect(a, "Shoe body", w * 0.35, h * 0.38, w * 0.44, h * 0.2, accent, 18);
      rect(a, "Shoe sole", w * 0.18, h * 0.58, w * 0.66, h * 0.08, c.ink, 8);
      ellipse(a, "Shoe lace", w * 0.52, h * 0.44, 8, 8, c.surface);
    } else if (type === "bag") {
      rect(a, "Bag body", w * 0.32, h * 0.33, w * 0.38, h * 0.42, accent, 10);
      ellipse(a, "Bag handle", w * 0.39, h * 0.2, w * 0.24, h * 0.22, bg, c.ink);
      rect(a, "Bag clasp", w * 0.47, h * 0.52, w * 0.08, h * 0.06, c.amber, 4);
    } else {
      rect(a, "Shoulder left", w * 0.26, h * 0.27, w * 0.16, h * 0.42, accent, 10);
      rect(a, "Shoulder right", w * 0.58, h * 0.27, w * 0.16, h * 0.42, accent, 10);
      rect(a, "Dress body", w * 0.36, h * 0.22, w * 0.28, h * 0.56, accent, 12);
      rect(a, "Neck", w * 0.45, h * 0.2, w * 0.1, h * 0.06, c.surface, 8);
    }
    ellipse(a, "Product shine", w * 0.68, h * 0.12, w * 0.18, w * 0.18, c.surface);
    return a;
  }

  function actionTo(destinationId) {
    const action = {
      type: "NODE",
      destinationId,
      navigation: "NAVIGATE",
      transition: { type: "SMART_ANIMATE", easing: { type: "EASE_OUT" }, duration: 0.2 },
      resetScrollPosition: true
    };
    return [{ trigger: { type: "ON_CLICK" }, action, actions: [action] }];
  }

  async function link(node, destination) {
    if (node && node.setReactionsAsync) await node.setReactionsAsync(actionTo(destination.id));
  }

  async function header(screen, active) {
    rect(screen, "Trust Bar", 0, 0, 1440, 34, c.ink);
    await text(screen, "Trust Copy", "100% original brands    |    7-day easy returns    |    Fast delivery with style protection", 64, 9, 12, "medium", c.surface);
    await text(screen, "Download App", "Download app", 1248, 9, 12, "medium", c.surface);

    rect(screen, "Header Surface", 0, 34, 1440, 76, c.surface);
    rect(screen, "Logo Mark", 64, 54, 42, 42, c.brand, 10);
    await text(screen, "Logo A", "A", 78, 63, 20, "bold", c.surface);
    await text(screen, "Logo Text", "Auraa", 118, 58, 26, "bold", c.ink);
    await text(screen, "Logo Sub", "fashion marketplace", 119, 84, 10, "medium", c.muted);

    const search = frame(screen, "Search Input", 284, 50, 570, 46, c.page, 8);
    search.strokes = [paint(c.line)];
    search.strokeWeight = 1;
    await text(search, "Search Label", "Search dresses, sneakers, watches, brands", 18, 14, 14, "regular", c.muted, 420);
    await pill(search, "AI Fit Pill", "AI fit", 470, 6, c.roseSoft, c.brandDark, 76);

    await text(screen, "Location", "Deliver to 560001", 894, 60, 13, "semibold", c.ink);
    await text(screen, "Location Sub", "Change pincode", 894, 80, 11, "regular", c.muted);

    const headerItems = [["Orders", 1040], ["Wishlist", 1134], ["Bag", 1238], ["Profile", 1310]];
    for (const item of headerItems) {
      ellipse(screen, item[0] + " Icon", item[1], 55, 34, 34, item[0] === "Bag" ? c.roseSoft : c.page, c.line);
      await text(screen, item[0] + " Text", item[0], item[1] - 6, 91, 11, "medium", c.text);
    }

    rect(screen, "Nav Surface", 0, 110, 1440, 54, c.surface);
    line(screen, "Nav Bottom Rule", 0, 163, 1440);
    const nav = ["Women", "Men", "Luxury", "Sneakers", "Beauty", "Home", "Sale"];
    let x = 64;
    for (const item of nav) {
      const isActive = item === active;
      await pill(screen, "Nav " + item, item, x, 120, isActive ? c.roseSoft : c.surface, isActive ? c.brandDark : c.text, item === "Sneakers" ? 96 : 84);
      x += item === "Sneakers" ? 112 : 94;
    }
  }

  async function homeScreen(screen) {
    await header(screen, "Women");

    const hero = frame(screen, "Editorial Fashion Hero", 64, 190, 850, 326, c.navy, 8);
    hero.clipsContent = true;
    rect(hero, "Hero warm block", 570, 0, 280, 326, c.cream);
    modelArt(hero, "Hero model", 588, 28, 210, 270, c.roseSoft, c.brand, "#E8B08B");
    await text(hero, "Hero Label", "New season edit", 42, 42, 14, "semibold", c.amber);
    await text(hero, "Hero Title", "Fashion discovery with checkout confidence.", 42, 76, 45, "bold", c.surface, 500);
    await text(hero, "Hero Body", "A curated shopping experience with Myntra-style trends and Amazon-style trust, delivery, reviews, and returns.", 42, 206, 17, "regular", "#D6E3FF", 520);
    const shopNow = await button(hero, "Shop Collection", "Shop collection", 42, 262, 150, 46, "orange");
    const viewLooks = await button(hero, "View Looks", "View looks", 208, 262, 124, 46, "ghost");

    const promise = frame(screen, "Marketplace Promise Panel", 944, 190, 432, 326, c.surface, 8);
    promise.strokes = [paint(c.line)];
    promise.strokeWeight = 1;
    shadow(promise, 0.08);
    await text(promise, "Promise Title", "Why this works better", 28, 28, 24, "bold", c.ink);
    const bullets = [
      ["Curated trends", "Fashion-first categories, looks, and recommendations."],
      ["Trust at purchase", "Verified brands, reviews, returns, and delivery dates."],
      ["Faster decisions", "Size guide, filters, offers, and cart clarity."]
    ];
    for (let i = 0; i < bullets.length; i += 1) {
      const y = 92 + i * 70;
      ellipse(promise, "Promise Icon " + i, 28, y, 42, 42, i === 0 ? c.roseSoft : i === 1 ? c.mint : c.blueSoft);
      await text(promise, "Promise Heading " + i, bullets[i][0], 88, y - 1, 16, "bold", c.ink);
      await text(promise, "Promise Body " + i, bullets[i][1], 88, y + 23, 13, "regular", c.muted, 300);
    }

    await text(screen, "Trend Title", "Shop by mood", 64, 558, 25, "bold", c.ink);
    const moods = [
      ["Office fits", c.blueSoft, c.navy],
      ["Wedding guest", c.roseSoft, c.brandDark],
      ["Sneaker drops", c.mint, c.teal],
      ["Festive edit", c.cream, c.orange],
      ["Luxury bags", c.lavender, c.violet]
    ];
    let mx = 64;
    for (const m of moods) {
      const card = frame(screen, "Mood " + m[0], mx, 608, 244, 138, m[1], 8);
      await text(card, "Mood Label", m[0], 18, 18, 20, "bold", c.ink, 160);
      await text(card, "Mood CTA", "Explore", 18, 94, 14, "semibold", m[2]);
      productArt(card, "Mood visual", 140, 30, 78, 86, c.surface, m[2], m[0].includes("Sneaker") ? "shoe" : m[0].includes("bags") ? "bag" : "dress");
      mx += 266;
    }

    await text(screen, "Recommendation Title", "Recommended for your style", 64, 790, 25, "bold", c.ink);
    const productButtons = [];
    const products = [
      ["Linen Wrap Dress", "Auraa Select", "Rs. 2,299", "4.6", "Free delivery by Apr 24", c.roseSoft, c.brand, "dress"],
      ["Street Court Sneakers", "NorthStep", "Rs. 3,499", "4.8", "Prime-style fast ship", c.mint, c.teal, "shoe"],
      ["Structured Tote Bag", "Mira Atelier", "Rs. 4,299", "4.5", "7-day returns", c.lavender, c.violet, "bag"],
      ["Relaxed Blazer", "Urban Loom", "Rs. 2,899", "4.7", "Original brand", c.blueSoft, c.navy, "dress"]
    ];
    for (let i = 0; i < products.length; i += 1) {
      productButtons.push(await productCard(screen, 64 + i * 328, 842, products[i]));
    }
    screen.actions = { shopNow, viewLooks, productButtons };
  }

  async function productCard(parent, x, y, p) {
    const card = frame(parent, p[0] + " Product Card", x, y, 296, 300, c.surface, 8);
    card.strokes = [paint(c.line)];
    card.strokeWeight = 1;
    shadow(card, 0.07);
    productArt(card, "Product Photo", 14, 14, 268, 134, p[5], p[6], p[7]);
    await pill(card, "Verified Badge", "Verified", 26, 26, c.surface, p[6], 86);
    await text(card, "Brand", p[1], 16, 164, 13, "semibold", c.muted);
    await text(card, "Name", p[0], 16, 186, 17, "bold", c.ink, 250);
    await text(card, "Rating", p[3] + " rating", 16, 225, 13, "medium", c.green);
    await text(card, "Delivery", p[4], 104, 225, 13, "regular", c.muted, 170);
    await text(card, "Price", p[2], 16, 256, 20, "bold", c.ink);
    const buy = await button(card, "Quick Buy", "Quick buy", 168, 250, 112, 38, "primary");
    return { card, buy };
  }

  async function listingScreen(screen) {
    await header(screen, "Women");
    await text(screen, "Listing Breadcrumb", "Home / Women / Westernwear", 64, 190, 13, "medium", c.muted);
    await text(screen, "Listing Title", "Women fashion", 64, 220, 32, "bold", c.ink);
    await text(screen, "Listing Count", "12,482 styles with verified delivery and returns", 64, 264, 15, "regular", c.muted);

    const filter = frame(screen, "Filter Sidebar", 64, 320, 254, 640, c.surface, 8);
    filter.strokes = [paint(c.line)];
    filter.strokeWeight = 1;
    await text(filter, "Filter Title", "Filters", 22, 22, 20, "bold", c.ink);
    await text(filter, "Filter Reset", "Clear all", 172, 27, 13, "semibold", c.brand);
    const groups = [
      ["Category", ["Dresses", "Tops", "Sneakers", "Bags"]],
      ["Brand", ["Auraa Select", "NorthStep", "Mira", "Urban Loom"]],
      ["Delivery", ["Tomorrow", "Free delivery", "Easy returns"]],
      ["Price", ["Rs. 999-1999", "Rs. 2000-3999", "Rs. 4000+"]]
    ];
    let gy = 76;
    for (const group of groups) {
      await text(filter, "Filter " + group[0], group[0], 22, gy, 14, "bold", c.ink);
      gy += 32;
      for (const item of group[1]) {
        rect(filter, "Check " + item, 22, gy + 2, 16, 16, c.surface, 4, c.line);
        await text(filter, "Check Text " + item, item, 50, gy, 13, "regular", c.text);
        gy += 28;
      }
      line(filter, "Filter Rule " + group[0], 22, gy + 8, 210);
      gy += 32;
    }

    const sort = frame(screen, "Sort Row", 344, 320, 1032, 56, c.surface, 8);
    sort.strokes = [paint(c.line)];
    sort.strokeWeight = 1;
    await text(sort, "Sort Copy", "Showing personalized picks based on browsing, size profile, rating, and fast delivery.", 20, 18, 14, "regular", c.muted);
    await pill(sort, "Sort Pill", "Sort: Recommended", 840, 11, c.page, c.ink, 168);

    const products = [
      ["Linen Wrap Dress", "Auraa Select", "Rs. 2,299", "4.6", "Free delivery by Apr 24", c.roseSoft, c.brand, "dress"],
      ["Street Court Sneakers", "NorthStep", "Rs. 3,499", "4.8", "Prime-style fast ship", c.mint, c.teal, "shoe"],
      ["Structured Tote Bag", "Mira Atelier", "Rs. 4,299", "4.5", "7-day returns", c.lavender, c.violet, "bag"],
      ["Relaxed Blazer", "Urban Loom", "Rs. 2,899", "4.7", "Original brand", c.blueSoft, c.navy, "dress"],
      ["Pleated Midi Dress", "Studio Auraa", "Rs. 1,999", "4.4", "Exchange available", c.cream, c.orange, "dress"],
      ["Canvas Day Tote", "Mira Atelier", "Rs. 2,199", "4.6", "Free returns", c.mint, c.teal, "bag"]
    ];
    const productButtons = [];
    for (let i = 0; i < products.length; i += 1) {
      const row = Math.floor(i / 3);
      const col = i % 3;
      productButtons.push(await productCard(screen, 344 + col * 344, 408 + row * 316, products[i]));
    }
    screen.actions = { productButtons };
  }

  async function productScreen(screen) {
    await header(screen, "Women");
    await text(screen, "Product Breadcrumb", "Home / Women / Dresses / Linen Wrap Dress", 64, 190, 13, "medium", c.muted);
    const gallery = frame(screen, "Product Gallery", 64, 230, 608, 650, c.surface, 8);
    gallery.strokes = [paint(c.line)];
    gallery.strokeWeight = 1;
    modelArt(gallery, "Large Product Visual", 118, 46, 370, 470, c.roseSoft, c.brand, "#E8B08B");
    const thumbs = [c.roseSoft, c.mint, c.blueSoft, c.lavender];
    for (let i = 0; i < thumbs.length; i += 1) productArt(gallery, "Thumbnail " + i, 38 + i * 136, 548, 104, 74, thumbs[i], i === 0 ? c.brand : c.teal, i === 2 ? "shoe" : "dress");

    await text(screen, "Product Brand", "Auraa Select", 736, 236, 15, "semibold", c.brand);
    await text(screen, "Product Title", "Linen Wrap Dress", 736, 268, 42, "bold", c.ink, 460);
    await text(screen, "Product Rating", "4.6 rating | 2,184 verified reviews | 7-day returns", 736, 348, 15, "medium", c.green, 500);
    await text(screen, "Product Price", "Rs. 2,299", 736, 394, 32, "bold", c.ink);
    await text(screen, "Product Offer", "Includes bank offers, free delivery, and exchange protection.", 736, 440, 15, "regular", c.muted, 500);

    const sizePanel = frame(screen, "Size Panel", 736, 492, 530, 154, c.surface, 8);
    sizePanel.strokes = [paint(c.line)];
    sizePanel.strokeWeight = 1;
    await text(sizePanel, "Size Title", "Select size", 22, 20, 18, "bold", c.ink);
    await text(sizePanel, "Size Guide", "Fit guide", 424, 23, 14, "semibold", c.brand);
    const sizes = ["XS", "S", "M", "L", "XL"];
    for (let i = 0; i < sizes.length; i += 1) {
      rect(sizePanel, "Size " + sizes[i], 22 + i * 62, 66, 46, 46, i === 2 ? c.ink : c.surface, 8, c.line);
      await text(sizePanel, "Size Text " + sizes[i], sizes[i], 35 + i * 62, 79, 14, "bold", i === 2 ? c.surface : c.ink);
    }

    const addCart = await button(screen, "Add To Bag", "Add to bag", 736, 682, 170, 50, "primary");
    const buyNow = await button(screen, "Buy Now", "Buy now", 924, 682, 146, 50, "orange");
    const wishlist = await button(screen, "Wishlist", "Wishlist", 1088, 682, 126, 50, "ghost");

    const delivery = frame(screen, "Delivery Panel", 736, 766, 530, 156, c.surface, 8);
    delivery.strokes = [paint(c.line)];
    delivery.strokeWeight = 1;
    await text(delivery, "Delivery Title", "Delivery and trust", 22, 20, 18, "bold", c.ink);
    await text(delivery, "Pincode", "Deliver to 560001 by Apr 24", 22, 58, 15, "semibold", c.ink);
    await text(delivery, "Trust", "100% original brand | Easy returns | Secure payment", 22, 88, 14, "regular", c.muted, 430);
    screen.actions = { addCart, buyNow, wishlist };
  }

  async function cartScreen(screen) {
    await header(screen, "Women");
    await text(screen, "Cart Heading", "Bag", 64, 198, 34, "bold", c.ink);
    await text(screen, "Cart Sub", "Clear item details, delivery promises, and total cost before checkout.", 64, 244, 16, "regular", c.muted);
    const items = frame(screen, "Bag Items", 64, 304, 828, 492, c.surface, 8);
    items.strokes = [paint(c.line)];
    items.strokeWeight = 1;
    const rows = [
      ["Linen Wrap Dress", "Size M | Qty 1", "Rs. 2,299", c.roseSoft, "dress"],
      ["Structured Tote Bag", "Qty 1", "Rs. 4,299", c.lavender, "bag"]
    ];
    for (let i = 0; i < rows.length; i += 1) {
      const y = 30 + i * 210;
      productArt(items, "Bag visual " + i, 28, y, 154, 150, rows[i][3], i === 0 ? c.brand : c.violet, rows[i][4]);
      await text(items, "Bag item name " + i, rows[i][0], 214, y + 8, 21, "bold", c.ink);
      await text(items, "Bag item meta " + i, rows[i][1], 214, y + 48, 14, "regular", c.muted);
      await text(items, "Bag item delivery " + i, "Free delivery by Apr 24 | Easy returns", 214, y + 84, 14, "medium", c.green);
      await text(items, "Bag item price " + i, rows[i][2], 660, y + 42, 22, "bold", c.ink);
      if (i === 0) line(items, "Bag divider", 28, y + 184, 772);
    }

    const summary = frame(screen, "Bag Summary", 948, 304, 428, 430, c.surface, 8);
    summary.strokes = [paint(c.line)];
    summary.strokeWeight = 1;
    shadow(summary, 0.08);
    await text(summary, "Summary title", "Price details", 28, 28, 24, "bold", c.ink);
    await text(summary, "Summary rows", "Items total                 Rs. 6,598\nDiscount                    -Rs. 700\nDelivery                    Free\nStyle protection            Rs. 49", 28, 90, 16, "regular", c.muted, 350);
    line(summary, "Summary divider", 28, 214, 372);
    await text(summary, "Summary total label", "Total", 28, 248, 20, "bold", c.ink);
    await text(summary, "Summary total", "Rs. 5,947", 288, 246, 24, "bold", c.ink);
    const checkout = await button(summary, "Checkout Button", "Proceed to checkout", 28, 334, 372, 52, "primary");
    screen.actions = { checkout };
  }

  async function checkoutScreen(screen) {
    await header(screen, "Women");
    await text(screen, "Checkout Heading", "Secure checkout", 64, 198, 34, "bold", c.ink);
    await text(screen, "Checkout Sub", "Amazon-like clarity with fashion-specific fit, exchange, and delivery confidence.", 64, 244, 16, "regular", c.muted);
    const address = frame(screen, "Address Card", 64, 304, 620, 222, c.surface, 8);
    address.strokes = [paint(c.line)];
    address.strokeWeight = 1;
    await text(address, "Address title", "Delivery address", 28, 28, 22, "bold", c.ink);
    rect(address, "Address input", 28, 82, 564, 84, c.page, 8, c.line);
    await text(address, "Address copy", "Varshith Kumar\nBengaluru, Karnataka 560001", 46, 100, 15, "regular", c.text, 360);

    const payment = frame(screen, "Payment Card", 64, 558, 620, 260, c.surface, 8);
    payment.strokes = [paint(c.line)];
    payment.strokeWeight = 1;
    await text(payment, "Payment title", "Payment method", 28, 28, 22, "bold", c.ink);
    await pill(payment, "UPI option", "UPI", 28, 82, c.mint, c.teal, 72);
    await pill(payment, "Card option", "Card", 116, 82, c.page, c.muted, 82);
    await pill(payment, "COD option", "Cash on delivery", 214, 82, c.page, c.muted, 154);
    rect(payment, "UPI input", 28, 150, 564, 50, c.page, 8, c.line);
    await text(payment, "UPI copy", "varshith@bank", 46, 166, 15, "regular", c.text);

    const final = frame(screen, "Final Order", 748, 304, 520, 514, c.surface, 8);
    final.strokes = [paint(c.line)];
    final.strokeWeight = 1;
    shadow(final, 0.08);
    await text(final, "Final title", "Order review", 28, 28, 24, "bold", c.ink);
    productArt(final, "Final art", 28, 86, 104, 100, c.roseSoft, c.brand, "dress");
    await text(final, "Final item", "Linen Wrap Dress + Tote Bag", 156, 96, 18, "bold", c.ink, 280);
    await text(final, "Final meta", "Arrives Apr 24 | Free delivery | Easy returns", 156, 136, 14, "regular", c.green, 300);
    line(final, "Final divider", 28, 230, 464);
    await text(final, "Final costs", "Items                         Rs. 6,598\nDiscount                      -Rs. 700\nStyle protection              Rs. 49", 28, 268, 16, "regular", c.muted, 380);
    await text(final, "Final total label", "Pay now", 28, 392, 20, "bold", c.ink);
    await text(final, "Final total", "Rs. 5,947", 328, 390, 24, "bold", c.ink);
    const place = await button(final, "Place Order", "Place order", 28, 444, 464, 48, "teal");
    screen.actions = { place };
  }

  async function label(page, target, value) {
    await text(page, "Frame Label " + value, value, target.x, target.y - 42, 22, "bold", c.ink);
  }

  const page = figma.createPage();
  page.name = pageName("Fashion Marketplace UX");
  await figma.setCurrentPageAsync(page);

  const home = frame(page, "Home - Auraa Fashion Marketplace", 0, 72, 1440, 1024, c.page);
  const listing = frame(page, "Listing - Filtered Fashion Search", 1560, 72, 1440, 1024, c.page);
  const product = frame(page, "Product - Fashion Detail", 3120, 72, 1440, 1024, c.page);
  const cart = frame(page, "Cart - Bag Review", 4680, 72, 1440, 1024, c.page);
  const checkout = frame(page, "Checkout - Secure Fashion Order", 6240, 72, 1440, 1024, c.page);

  await homeScreen(home);
  await listingScreen(listing);
  await productScreen(product);
  await cartScreen(cart);
  await checkoutScreen(checkout);

  await label(page, home, "Home");
  await label(page, listing, "Listing");
  await label(page, product, "Product");
  await label(page, cart, "Cart");
  await label(page, checkout, "Checkout");

  await link(home.actions.shopNow, listing);
  await link(home.actions.viewLooks, listing);
  for (const item of home.actions.productButtons) {
    await link(item.card, product);
    await link(item.buy, product);
  }
  for (const item of listing.actions.productButtons) {
    await link(item.card, product);
    await link(item.buy, product);
  }
  await link(product.actions.addCart, cart);
  await link(product.actions.buyNow, checkout);
  await link(cart.actions.checkout, checkout);
  await link(checkout.actions.place, home);

  figma.currentPage.selection = [home, listing, product, cart, checkout];
  figma.viewport.scrollAndZoomIntoView([home, listing, product, cart, checkout]);
  figma.closePlugin("Created an authentic fashion marketplace UI/UX prototype.");
})();
