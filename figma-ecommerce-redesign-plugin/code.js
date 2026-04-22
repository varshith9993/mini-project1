(async function () {
  const palette = {
    ink: "#172033",
    muted: "#667085",
    line: "#E4E7EC",
    page: "#F6F7FB",
    surface: "#FFFFFF",
    brand: "#0F766E",
    brandDark: "#115E59",
    orange: "#F97316",
    amber: "#FACC15",
    violet: "#7C3AED",
    blue: "#2563EB",
    mint: "#CCFBF1",
    coral: "#FFE4D5",
    lavender: "#EDE9FE"
  };

  const font = await resolveFont();

  async function resolveFont() {
    const preferred = [
      { family: "Inter", style: "Regular" },
      { family: "Inter", style: "Medium" },
      { family: "Inter", style: "Semi Bold" },
      { family: "Inter", style: "Bold" }
    ];

    try {
      for (const item of preferred) {
        await figma.loadFontAsync(item);
      }
      return {
        family: "Inter",
        regular: "Regular",
        medium: "Medium",
        semibold: "Semi Bold",
        bold: "Bold"
      };
    } catch (error) {
      const fonts = await figma.listAvailableFontsAsync();
      const fallback = fonts[0] ? fonts[0].fontName : { family: "Arial", style: "Regular" };
      await figma.loadFontAsync(fallback);
      return {
        family: fallback.family,
        regular: fallback.style,
        medium: fallback.style,
        semibold: fallback.style,
        bold: fallback.style
      };
    }
  }

  function hexToRgb(hex) {
    const raw = hex.replace("#", "");
    const value = parseInt(raw.length === 3 ? raw.split("").map((c) => c + c).join("") : raw, 16);
    return {
      r: ((value >> 16) & 255) / 255,
      g: ((value >> 8) & 255) / 255,
      b: (value & 255) / 255
    };
  }

  function solid(hex, opacity) {
    return {
      type: "SOLID",
      color: hexToRgb(hex),
      opacity: opacity === undefined ? 1 : opacity
    };
  }

  function makePageName(base) {
    const names = figma.root.children.map((page) => page.name);
    if (!names.includes(base)) return base;
    let index = 2;
    while (names.includes(base + " " + index)) index += 1;
    return base + " " + index;
  }

  function addFrame(parent, name, x, y, width, height, fill, radius) {
    const node = figma.createFrame();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(width, height);
    node.fills = [solid(fill || palette.surface)];
    node.strokes = [];
    node.clipsContent = false;
    if (radius) node.cornerRadius = radius;
    return node;
  }

  function addRect(parent, name, x, y, width, height, fill, radius, stroke) {
    const node = figma.createRectangle();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(width, height);
    node.fills = [solid(fill || palette.surface)];
    if (stroke) {
      node.strokes = [solid(stroke)];
      node.strokeWeight = 1;
    } else {
      node.strokes = [];
    }
    if (radius) node.cornerRadius = radius;
    return node;
  }

  function addEllipse(parent, name, x, y, width, height, fill, stroke) {
    const node = figma.createEllipse();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    node.resize(width, height);
    node.fills = [solid(fill || palette.surface)];
    if (stroke) {
      node.strokes = [solid(stroke)];
      node.strokeWeight = 1;
    } else {
      node.strokes = [];
    }
    return node;
  }

  async function addText(parent, name, value, x, y, size, weight, fill, width) {
    const node = figma.createText();
    node.name = name;
    parent.appendChild(node);
    node.x = x;
    node.y = y;
    const style = font[weight || "regular"] || font.regular;
    node.fontName = { family: font.family, style };
    node.fontSize = size;
    node.lineHeight = { unit: "PIXELS", value: Math.round(size * 1.28) };
    node.letterSpacing = { unit: "PERCENT", value: 0 };
    node.fills = [solid(fill || palette.ink)];
    node.characters = value;
    if (width) {
      node.textAutoResize = "HEIGHT";
      node.resize(width, node.height);
    } else {
      node.textAutoResize = "WIDTH_AND_HEIGHT";
    }
    return node;
  }

  function addShadow(node, strength) {
    const alpha = strength || 0.12;
    node.effects = [{
      type: "DROP_SHADOW",
      color: { r: 23 / 255, g: 32 / 255, b: 51 / 255, a: alpha },
      offset: { x: 0, y: 10 },
      radius: 24,
      spread: -4,
      visible: true,
      blendMode: "NORMAL"
    }];
  }

  async function addButton(parent, name, x, y, width, height, label, variant) {
    const styles = {
      primary: { bg: palette.brand, text: palette.surface, stroke: null },
      dark: { bg: palette.ink, text: palette.surface, stroke: null },
      orange: { bg: palette.orange, text: palette.surface, stroke: null },
      ghost: { bg: palette.surface, text: palette.ink, stroke: palette.line },
      pale: { bg: palette.mint, text: palette.brandDark, stroke: null }
    };
    const style = styles[variant || "primary"];
    const button = addFrame(parent, name, x, y, width, height, style.bg, 8);
    button.layoutMode = "HORIZONTAL";
    button.primaryAxisAlignItems = "CENTER";
    button.counterAxisAlignItems = "CENTER";
    button.itemSpacing = 8;
    button.paddingLeft = 16;
    button.paddingRight = 16;
    button.paddingTop = 10;
    button.paddingBottom = 10;
    if (style.stroke) {
      button.strokes = [solid(style.stroke)];
      button.strokeWeight = 1;
    }
    const labelNode = await addText(button, name + " Label", label, 0, 0, 14, "semibold", style.text);
    labelNode.layoutAlign = "CENTER";
    return button;
  }

  async function addPill(parent, name, label, x, y, fill, textColor) {
    const pill = addFrame(parent, name, x, y, 120, 34, fill, 17);
    pill.layoutMode = "HORIZONTAL";
    pill.primaryAxisAlignItems = "CENTER";
    pill.counterAxisAlignItems = "CENTER";
    pill.paddingLeft = 14;
    pill.paddingRight = 14;
    const labelNode = await addText(pill, name + " Label", label, 0, 0, 13, "medium", textColor || palette.ink);
    labelNode.layoutAlign = "CENTER";
    pill.resize(Math.max(72, labelNode.width + 28), 34);
    return pill;
  }

  function navAction(destinationId) {
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
    if (node && node.setReactionsAsync) {
      await node.setReactionsAsync(navAction(destination.id));
    }
  }

  async function addSearch(parent, x, y) {
    const search = addFrame(parent, "Search Bar", x, y, 560, 44, palette.page, 8);
    search.strokes = [solid(palette.line)];
    search.strokeWeight = 1;
    await addText(search, "Search Icon", "Search", 18, 12, 13, "medium", palette.muted);
    await addText(search, "Search Placeholder", "Search for brands, styles, and categories", 88, 12, 14, "regular", palette.muted, 360);
    return search;
  }

  async function addHeader(screen) {
    addRect(screen, "Header Surface", 0, 0, 1440, 82, palette.surface);
    addRect(screen, "Header Rule", 0, 81, 1440, 1, palette.line);
    addRect(screen, "Logo Mark", 64, 22, 40, 40, palette.brand, 10);
    await addText(screen, "Logo Letter", "S", 78, 31, 18, "bold", palette.surface);
    await addText(screen, "Logo Text", "ShopFlow", 116, 27, 24, "bold", palette.ink);
    await addSearch(screen, 280, 19);

    const labels = [
      ["Wishlist", "Wish"],
      ["Cart", "Cart"],
      ["Profile", "You"]
    ];
    for (let i = 0; i < labels.length; i += 1) {
      const x = 1058 + i * 100;
      addEllipse(screen, labels[i][0] + " Icon Circle", x, 21, 40, 40, i === 1 ? palette.coral : palette.page, palette.line);
      await addText(screen, labels[i][0] + " Icon Label", labels[i][1], x + 6, 34, 11, "semibold", i === 1 ? palette.orange : palette.ink);
    }
  }

  async function addNav(screen) {
    addRect(screen, "Navigation Surface", 0, 82, 1440, 54, palette.surface);
    const items = ["Women", "Men", "Beauty", "Home", "Kids", "Deals", "New"];
    let x = 64;
    for (let i = 0; i < items.length; i += 1) {
      const active = i === 0;
      await addPill(screen, "Nav " + items[i], items[i], x, 92, active ? palette.mint : palette.surface, active ? palette.brandDark : palette.muted);
      x += active ? 102 : 88;
    }
  }

  async function addHome(screen) {
    await addHeader(screen);
    await addNav(screen);

    const hero = addFrame(screen, "Hero Banner", 64, 158, 1312, 270, palette.ink, 8);
    hero.clipsContent = true;
    addRect(hero, "Hero Mint Panel", 760, 0, 552, 270, palette.mint);
    addRect(hero, "Hero Coral Block", 1045, 42, 180, 178, palette.coral, 8);
    addRect(hero, "Hero Violet Block", 858, 76, 152, 130, palette.lavender, 8);
    addEllipse(hero, "Hero Accent 1", 1198, 154, 96, 96, palette.amber);
    await addText(hero, "Hero Eyebrow", "Student UI/UX ecommerce redesign", 44, 42, 14, "semibold", palette.amber);
    await addText(hero, "Hero Title", "Find the right product faster.", 44, 72, 44, "bold", palette.surface, 520);
    await addText(hero, "Hero Copy", "A cleaner marketplace layout with clearer navigation, sharper product cards, and direct cart and checkout actions.", 44, 186, 17, "regular", "#D0D5DD", 570);
    const heroBuy = await addButton(hero, "Hero Buy Now", 44, 220, 130, 44, "Buy Now", "orange");
    const heroExplore = await addButton(hero, "Hero Explore Deals", 188, 220, 150, 44, "Explore Deals", "ghost");
    await addText(hero, "Hero Metric 1", "24% faster", 826, 28, 28, "bold", palette.brandDark);
    await addText(hero, "Hero Metric Label 1", "navigation path", 826, 64, 14, "medium", palette.brandDark);
    await addText(hero, "Hero Metric 2", "More confident", 1048, 62, 24, "bold", palette.ink);
    await addText(hero, "Hero Metric Label 2", "buy decisions", 1048, 96, 14, "medium", palette.muted);
    screen.heroButtons = [heroBuy, heroExplore];

    await addText(screen, "Products Title", "Recommended for you", 64, 462, 28, "bold", palette.ink);
    await addText(screen, "Products Subtitle", "Personalized product cards with fast action buttons.", 64, 499, 15, "regular", palette.muted);

    const products = [
      ["Everyday Denim Jacket", "Rs. 1,899", "4.8 / 5", palette.mint, palette.brand],
      ["Minimal Running Shoes", "Rs. 2,499", "4.7 / 5", palette.coral, palette.orange],
      ["Soft Cotton Co-ord", "Rs. 1,299", "4.6 / 5", palette.lavender, palette.violet],
      ["Classic Smart Watch", "Rs. 3,299", "4.9 / 5", "#DBEAFE", palette.blue]
    ];

    screen.productButtons = [];
    for (let i = 0; i < products.length; i += 1) {
      const x = 64 + i * 328;
      const buttons = await addProductCard(screen, x, 540, products[i]);
      screen.productButtons.push(buttons);
    }

    const impact = addFrame(screen, "UX Impact Strip", 64, 920, 1312, 64, palette.surface, 8);
    impact.strokes = [solid(palette.line)];
    impact.strokeWeight = 1;
    await addText(impact, "Impact Title", "UX improvements", 24, 14, 17, "bold", palette.ink);
    await addText(impact, "Impact Copy", "Simpler navigation, clean layout, recommendations, and stronger conversion flow.", 190, 16, 15, "regular", palette.muted, 760);
    await addPill(impact, "Conversion Pill", "Conversion focus", 1090, 15, palette.coral, palette.orange);
  }

  async function addProductCard(parent, x, y, product) {
    const card = addFrame(parent, product[0] + " Card", x, y, 296, 344, palette.surface, 8);
    card.strokes = [solid(palette.line)];
    card.strokeWeight = 1;
    addShadow(card, 0.08);
    addRect(card, "Product Image", 16, 16, 264, 148, product[3], 8);
    addEllipse(card, "Product Accent", 194, 44, 56, 56, product[4]);
    addRect(card, "Product Block", 48, 78, 128, 50, palette.surface, 8);
    await addPill(card, "Product Tag", "Top rated", 26, 26, palette.surface, product[4]);
    await addText(card, "Product Name", product[0], 16, 184, 18, "bold", palette.ink, 248);
    await addText(card, "Product Rating", product[2], 16, 236, 14, "medium", palette.muted);
    await addText(card, "Product Price", product[1], 188, 234, 20, "bold", palette.ink);
    const add = await addButton(card, "Add To Cart", 16, 286, 126, 40, "Add to Cart", "ghost");
    const buy = await addButton(card, "Buy Now", 154, 286, 126, 40, "Buy Now", "primary");
    return { add, buy, card };
  }

  async function addProductScreen(screen) {
    await addHeader(screen);
    await addText(screen, "Breadcrumb", "Home / Women / Jacket", 64, 118, 14, "medium", palette.muted);
    const image = addFrame(screen, "Product Detail Image", 64, 170, 620, 640, palette.mint, 8);
    addRect(image, "Image Panel", 80, 92, 420, 410, palette.surface, 8);
    addEllipse(image, "Image Accent", 400, 74, 132, 132, palette.brand);
    addRect(image, "Image Base", 148, 526, 324, 52, palette.brandDark, 8);

    await addText(screen, "Product Detail Label", "Product detail", 744, 178, 14, "semibold", palette.brand);
    await addText(screen, "Product Detail Title", "Everyday Denim Jacket", 744, 210, 42, "bold", palette.ink, 440);
    await addText(screen, "Product Detail Copy", "Lightweight layer with durable stitching, clear sizing, reviews, and quick checkout decisions.", 744, 326, 17, "regular", palette.muted, 500);
    await addText(screen, "Product Detail Price", "Rs. 1,899", 744, 416, 30, "bold", palette.ink);
    await addText(screen, "Product Detail Rating", "4.8 / 5 rating    2.4k reviews", 744, 464, 15, "medium", palette.muted);

    await addText(screen, "Size Label", "Select size", 744, 526, 16, "bold", palette.ink);
    const sizes = ["S", "M", "L", "XL"];
    for (let i = 0; i < sizes.length; i += 1) {
      addRect(screen, "Size " + sizes[i], 744 + i * 58, 558, 42, 42, i === 1 ? palette.ink : palette.surface, 8, palette.line);
      await addText(screen, "Size " + sizes[i] + " Text", sizes[i], 758 + i * 58, 570, 14, "bold", i === 1 ? palette.surface : palette.ink);
    }

    const addCart = await addButton(screen, "Product Add To Cart", 744, 636, 184, 48, "Add to Cart", "primary");
    const buyNow = await addButton(screen, "Product Buy Now", 944, 636, 160, 48, "Buy Now", "orange");
    const backHome = await addButton(screen, "Back To Home", 1120, 636, 132, 48, "Home", "ghost");
    screen.actions = { addCart, buyNow, backHome };

    const note = addFrame(screen, "Product UX Note", 744, 736, 530, 112, palette.surface, 8);
    note.strokes = [solid(palette.line)];
    note.strokeWeight = 1;
    await addText(note, "Product UX Note Title", "UX decision", 22, 18, 16, "bold", palette.ink);
    await addText(note, "Product UX Note Copy", "Size, review, price, and direct action are grouped to reduce decision time.", 22, 48, 15, "regular", palette.muted, 452);
  }

  async function addCartScreen(screen) {
    await addHeader(screen);
    await addText(screen, "Cart Title", "Shopping cart", 64, 128, 34, "bold", palette.ink);
    await addText(screen, "Cart Subtitle", "Review items and move to checkout with fewer distractions.", 64, 176, 16, "regular", palette.muted);

    const list = addFrame(screen, "Cart List", 64, 236, 820, 520, palette.surface, 8);
    list.strokes = [solid(palette.line)];
    list.strokeWeight = 1;
    const cartItems = [
      ["Everyday Denim Jacket", "Size M", "Rs. 1,899", palette.mint],
      ["Minimal Running Shoes", "Size 8", "Rs. 2,499", palette.coral],
      ["Classic Smart Watch", "Black", "Rs. 3,299", "#DBEAFE"]
    ];
    for (let i = 0; i < cartItems.length; i += 1) {
      const y = 28 + i * 154;
      addRect(list, "Cart Item Image " + i, 28, y, 112, 112, cartItems[i][3], 8);
      await addText(list, "Cart Item Name " + i, cartItems[i][0], 166, y + 10, 18, "bold", palette.ink, 300);
      await addText(list, "Cart Item Meta " + i, cartItems[i][1] + "    Qty 1", 166, y + 48, 14, "regular", palette.muted);
      await addText(list, "Cart Item Price " + i, cartItems[i][2], 648, y + 36, 20, "bold", palette.ink);
      if (i < cartItems.length - 1) addRect(list, "Cart Divider " + i, 28, y + 138, 764, 1, palette.line);
    }

    const summary = addFrame(screen, "Cart Summary", 940, 236, 436, 424, palette.surface, 8);
    summary.strokes = [solid(palette.line)];
    summary.strokeWeight = 1;
    addShadow(summary, 0.08);
    await addText(summary, "Summary Title", "Order summary", 28, 28, 24, "bold", palette.ink);
    await addText(summary, "Summary Rows", "Subtotal                         Rs. 7,697\nShipping                         Free\nDiscount                         -Rs. 500", 28, 92, 16, "regular", palette.muted, 360);
    addRect(summary, "Summary Rule", 28, 202, 380, 1, palette.line);
    await addText(summary, "Summary Total Label", "Total", 28, 238, 18, "bold", palette.ink);
    await addText(summary, "Summary Total", "Rs. 7,197", 288, 236, 22, "bold", palette.ink);
    const checkout = await addButton(summary, "Proceed To Checkout", 28, 326, 380, 48, "Proceed to Checkout", "orange");
    const home = await addButton(summary, "Continue Shopping", 28, 262, 380, 46, "Continue Shopping", "ghost");
    screen.actions = { checkout, home };
  }

  async function addCheckoutScreen(screen) {
    await addHeader(screen);
    await addText(screen, "Checkout Title", "Checkout", 64, 128, 34, "bold", palette.ink);
    await addText(screen, "Checkout Subtitle", "Single-page checkout keeps the final task clear.", 64, 176, 16, "regular", palette.muted);

    const address = addFrame(screen, "Address Panel", 64, 236, 604, 284, palette.surface, 8);
    address.strokes = [solid(palette.line)];
    address.strokeWeight = 1;
    await addText(address, "Address Title", "Delivery address", 28, 28, 22, "bold", palette.ink);
    addRect(address, "Name Input", 28, 86, 548, 48, palette.page, 8, palette.line);
    addRect(address, "Address Input", 28, 150, 548, 84, palette.page, 8, palette.line);
    await addText(address, "Name Placeholder", "Varshith Kumar", 46, 102, 15, "regular", palette.ink);
    await addText(address, "Address Placeholder", "House no., street, city, state, pincode", 46, 170, 15, "regular", palette.muted);

    const payment = addFrame(screen, "Payment Panel", 64, 552, 604, 280, palette.surface, 8);
    payment.strokes = [solid(palette.line)];
    payment.strokeWeight = 1;
    await addText(payment, "Payment Title", "Payment method", 28, 28, 22, "bold", palette.ink);
    await addPill(payment, "UPI Pill", "UPI", 28, 86, palette.mint, palette.brandDark);
    await addPill(payment, "Card Pill", "Card", 116, 86, palette.page, palette.muted);
    await addPill(payment, "COD Pill", "Cash on delivery", 218, 86, palette.page, palette.muted);
    addRect(payment, "Payment Input", 28, 150, 548, 48, palette.page, 8, palette.line);
    await addText(payment, "Payment Placeholder", "name@bank", 46, 166, 15, "regular", palette.muted);

    const order = addFrame(screen, "Checkout Summary", 744, 236, 500, 500, palette.surface, 8);
    order.strokes = [solid(palette.line)];
    order.strokeWeight = 1;
    addShadow(order, 0.08);
    await addText(order, "Checkout Summary Title", "Final review", 28, 28, 24, "bold", palette.ink);
    await addText(order, "Checkout Summary Copy", "Items, delivery, payment, and total remain visible before the user confirms the order.", 28, 72, 16, "regular", palette.muted, 408);
    addRect(order, "Checkout Summary Image", 28, 156, 92, 92, palette.mint, 8);
    await addText(order, "Checkout Summary Item", "Everyday Denim Jacket", 146, 164, 17, "bold", palette.ink, 240);
    await addText(order, "Checkout Summary Meta", "Size M    Qty 1", 146, 200, 14, "regular", palette.muted);
    addRect(order, "Checkout Summary Rule", 28, 284, 444, 1, palette.line);
    await addText(order, "Checkout Summary Total Label", "Payable amount", 28, 320, 18, "bold", palette.ink);
    await addText(order, "Checkout Summary Total", "Rs. 7,197", 312, 318, 24, "bold", palette.ink);
    const place = await addButton(order, "Place Order", 28, 408, 444, 52, "Place Order", "primary");
    screen.actions = { place };
  }

  async function addScreenLabel(page, frame, label) {
    await addText(page, "Label " + label, label, frame.x, frame.y - 44, 22, "bold", palette.ink);
  }

  const page = figma.createPage();
  page.name = makePageName("Ecommerce UX Redesign");
  await figma.setCurrentPageAsync(page);

  const home = addFrame(page, "Home - Ecommerce Redesign", 0, 72, 1440, 1024, palette.page);
  const product = addFrame(page, "Product Detail", 1560, 72, 1440, 1024, palette.page);
  const cart = addFrame(page, "Cart", 3120, 72, 1440, 1024, palette.page);
  const checkout = addFrame(page, "Checkout", 4680, 72, 1440, 1024, palette.page);

  await addHome(home);
  await addProductScreen(product);
  await addCartScreen(cart);
  await addCheckoutScreen(checkout);

  await addScreenLabel(page, home, "Home");
  await addScreenLabel(page, product, "Product");
  await addScreenLabel(page, cart, "Cart");
  await addScreenLabel(page, checkout, "Checkout");

  await link(home.heroButtons[0], product);
  await link(home.heroButtons[1], product);
  for (const item of home.productButtons) {
    await link(item.card, product);
    await link(item.buy, product);
    await link(item.add, cart);
  }
  await link(product.actions.addCart, cart);
  await link(product.actions.buyNow, checkout);
  await link(product.actions.backHome, home);
  await link(cart.actions.checkout, checkout);
  await link(cart.actions.home, home);
  await link(checkout.actions.place, home);

  figma.currentPage.selection = [home, product, cart, checkout];
  figma.viewport.scrollAndZoomIntoView([home, product, cart, checkout]);
  figma.closePlugin("Created Ecommerce UX Redesign screens and prototype links.");
})();
