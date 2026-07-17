-- ==========================================
-- CATEGORIES
-- ==========================================

INSERT INTO categories
(id, name, description, active, created_at, updated_at, created_by, updated_by)
VALUES
(1, 'Electronics', 'Smartphones, laptops, accessories and electronic gadgets', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(2, 'Fashion', 'Men, women and kids clothing, apparel and accessories', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(3, 'Footwear', 'Running shoes, sneakers, sandals, boots and slippers', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(4, 'Home & Kitchen', 'Kitchen appliances, cookware, furniture and home essentials', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(5, 'Beauty & Personal Care', 'Skincare, cosmetics, haircare, perfumes and grooming products', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(6, 'Grocery', 'Rice, flour, oil, beverages, snacks and daily grocery essentials', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(7, 'Books', 'Programming books, novels, educational books and stationery', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(8, 'Sports & Fitness', 'Gym equipment, sports accessories and fitness products', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(9, 'Toys & Games', 'Kids toys, puzzles, action figures and board games', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM'),

(10, 'Automotive', 'Bike accessories, car accessories, engine oils and maintenance products', true, NOW(), NOW(), 'SYSTEM', 'SYSTEM');



-- =====================================================
-- PRODUCTS - CATEGORY 1 (MOBILES)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 1, 'Apple iPhone 16', 'Apple iPhone 16 with A18 chip, 128GB storage and Super Retina display.', 79999.00, 40, 'APPROVED', true),

(3, 1, 'Samsung Galaxy S25', 'Samsung flagship smartphone with Dynamic AMOLED display and Snapdragon processor.', 74999.00, 35, 'APPROVED', true),

(3, 1, 'OnePlus 13', 'Premium OnePlus smartphone with fast charging and Snapdragon chipset.', 59999.00, 50, 'APPROVED', true),

(3, 1, 'Google Pixel 10', 'Google Pixel smartphone with advanced AI camera and Android experience.', 69999.00, 30, 'APPROVED', true),

(3, 1, 'Nothing Phone 4', 'Nothing Phone featuring transparent design and Glyph interface.', 42999.00, 60, 'APPROVED', true),

(3, 1, 'Xiaomi 16 Pro', 'Xiaomi flagship smartphone with Leica camera and AMOLED display.', 54999.00, 45, 'APPROVED', true),

(3, 1, 'Realme GT 8 Pro', 'Realme performance smartphone with high refresh rate display.', 39999.00, 55, 'APPROVED', true),

(3, 1, 'Motorola Edge 70', 'Motorola smartphone with curved OLED display and clean Android.', 34999.00, 65, 'APPROVED', true),

(3, 1, 'Vivo X300', 'Vivo smartphone featuring ZEISS camera system and fast charging.', 52999.00, 28, 'APPROVED', true),

(3, 1, 'Oppo Find X9', 'Premium Oppo smartphone with powerful processor and AI photography.', 57999.00, 33, 'APPROVED', true);


-- =====================================================
-- PRODUCTS - CATEGORY 2 (FASHION)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 2, 'Men''s Cotton Round Neck T-Shirt', 'Premium 100% cotton round neck T-shirt suitable for everyday casual wear.', 799.00, 120, 'APPROVED', true),

(3, 2, 'Men''s Slim Fit Jeans', 'Comfortable stretch denim jeans with slim fit design and durable fabric.', 1899.00, 80, 'APPROVED', true),

(3, 2, 'Women''s Printed Kurti', 'Elegant printed cotton kurti perfect for office and casual occasions.', 1299.00, 90, 'APPROVED', true),

(3, 2, 'Women''s Silk Saree', 'Traditional silk saree with rich border design for festive occasions.', 4999.00, 40, 'APPROVED', true),

(3, 2, 'Men''s Formal Shirt', 'Full sleeve formal shirt made from wrinkle-resistant premium fabric.', 1599.00, 70, 'APPROVED', true),

(3, 2, 'Unisex Hooded Sweatshirt', 'Warm fleece hooded sweatshirt with front pocket and adjustable hood.', 1999.00, 60, 'APPROVED', true),

(3, 2, 'Women''s Denim Jacket', 'Classic blue denim jacket with regular fit and button closure.', 2499.00, 50, 'APPROVED', true),

(3, 2, 'Men''s Cargo Pants', 'Comfortable cargo pants featuring multiple utility pockets.', 1799.00, 75, 'APPROVED', true),

(3, 2, 'Kids Party Dress', 'Stylish and comfortable party wear dress designed for kids.', 1499.00, 65, 'APPROVED', true),

(3, 2, 'Women''s Palazzo Pants', 'Soft rayon palazzo pants offering comfort and modern style.', 999.00, 100, 'APPROVED', true);


-- =====================================================
-- PRODUCTS - CATEGORY 3 (FOOTWEAR)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 3, 'Nike Air Zoom Running Shoes', 'Lightweight running shoes with responsive cushioning for daily training.', 6499.00, 55, 'APPROVED', true),

(3, 3, 'Adidas Ultraboost Sneakers', 'Premium sneakers featuring Boost cushioning and breathable knit upper.', 8999.00, 40, 'APPROVED', true),

(3, 3, 'Puma RS-X Sports Shoes', 'Comfortable sports shoes with stylish design and excellent grip.', 4999.00, 60, 'APPROVED', true),

(3, 3, 'Woodland Leather Boots', 'Durable genuine leather boots suitable for trekking and outdoor adventures.', 5999.00, 35, 'APPROVED', true),

(3, 3, 'Crocs Classic Clogs', 'Lightweight waterproof clogs designed for all-day comfort.', 2999.00, 90, 'APPROVED', true),

(3, 3, 'Men''s Formal Leather Shoes', 'Elegant leather formal shoes ideal for office and business meetings.', 3499.00, 50, 'APPROVED', true),

(3, 3, 'Women''s High Heel Sandals', 'Stylish high heel sandals perfect for parties and special occasions.', 2799.00, 45, 'APPROVED', true),

(3, 3, 'Casual Canvas Sneakers', 'Comfortable everyday canvas sneakers with durable rubber sole.', 1999.00, 85, 'APPROVED', true),

(3, 3, 'Comfort Flip Flops', 'Soft and lightweight flip flops suitable for daily indoor and outdoor use.', 699.00, 150, 'APPROVED', true),

(3, 3, 'Orthopedic Walking Shoes', 'Walking shoes designed with superior cushioning and arch support.', 3999.00, 70, 'APPROVED', true);


-- =====================================================
-- PRODUCTS - CATEGORY 4 (HOME & KITCHEN)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 4, 'Prestige Pressure Cooker 5L', 'Durable stainless steel pressure cooker ideal for everyday cooking.', 2499.00, 60, 'APPROVED', true),

(3, 4, 'Non-Stick Frying Pan 28cm', 'Premium non-stick frying pan with heat-resistant handle.', 1299.00, 90, 'APPROVED', true),

(3, 4, 'Philips Mixer Grinder 750W', 'Powerful mixer grinder with three stainless steel jars.', 4299.00, 45, 'APPROVED', true),

(3, 4, 'Electric Rice Cooker 1.8L', 'Automatic rice cooker with keep-warm function.', 2799.00, 50, 'APPROVED', true),

(3, 4, 'Prestige Electric Kettle 1.5L', 'Fast boiling electric kettle with auto shut-off feature.', 1599.00, 80, 'APPROVED', true),

(3, 4, 'Milton Stainless Steel Water Bottle', '1-litre insulated stainless steel water bottle.', 899.00, 120, 'APPROVED', true),

(3, 4, 'Wooden Study Table', 'Engineered wood study table with storage shelves.', 6999.00, 25, 'APPROVED', true),

(3, 4, 'Ergonomic Office Chair', 'Comfortable office chair with adjustable height and lumbar support.', 8499.00, 20, 'APPROVED', true),

(3, 4, 'Air Fryer 4L', 'Oil-free air fryer with digital touch controls and preset cooking modes.', 5999.00, 35, 'APPROVED', true),

(3, 4, 'Microwave Oven 23L', '23-litre convection microwave oven suitable for baking and grilling.', 10499.00, 18, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 5 (BEAUTY & PERSONAL CARE)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 5, 'Himalaya Neem Face Wash', 'Deep cleansing face wash enriched with neem and turmeric for healthy skin.', 249.00, 150, 'APPROVED', true),

(3, 5, 'Nivea Soft Moisturizing Cream', 'Lightweight moisturizing cream suitable for face, hands and body.', 299.00, 120, 'APPROVED', true),

(3, 5, 'L''Oréal Paris Total Repair Shampoo', 'Repair shampoo formulated to strengthen damaged hair and reduce breakage.', 499.00, 100, 'APPROVED', true),

(3, 5, 'Dove Nourishing Hair Oil', 'Hair oil enriched with natural ingredients for smooth and healthy hair.', 349.00, 110, 'APPROVED', true),

(3, 5, 'Lakme Sun Expert SPF 50 Sunscreen', 'Broad-spectrum sunscreen providing protection against harmful UV rays.', 399.00, 90, 'APPROVED', true),

(3, 5, 'Maybelline Matte Lipstick', 'Long-lasting matte lipstick with rich color payoff and smooth finish.', 599.00, 80, 'APPROVED', true),

(3, 5, 'Wild Stone Premium Perfume', 'Long-lasting fragrance suitable for everyday and special occasions.', 899.00, 70, 'APPROVED', true),

(3, 5, 'Philips Beard Trimmer BT3211', 'Rechargeable beard trimmer with adjustable length settings.', 1999.00, 50, 'APPROVED', true),

(3, 5, 'Vaseline Intensive Care Body Lotion', 'Deep moisturizing body lotion for dry skin with non-greasy formula.', 349.00, 130, 'APPROVED', true),

(3, 5, 'Minimalist Vitamin C Face Serum', 'Brightening face serum enriched with Vitamin C for glowing skin.', 699.00, 75, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 6 (GROCERY)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 6, 'India Gate Basmati Rice 5kg', 'Premium long-grain basmati rice with rich aroma and excellent cooking quality.', 699.00, 120, 'APPROVED', true),

(3, 6, 'Aashirvaad Whole Wheat Flour 5kg', 'High-quality whole wheat flour for soft and nutritious rotis.', 329.00, 150, 'APPROVED', true),

(3, 6, 'Fortune Sunflower Cooking Oil 5L', 'Refined sunflower oil suitable for everyday healthy cooking.', 899.00, 100, 'APPROVED', true),

(3, 6, 'Tata Salt 1kg', 'Vacuum evaporated iodized salt for daily household cooking.', 30.00, 300, 'APPROVED', true),

(3, 6, 'Tata Tea Gold 1kg', 'Premium blend of tea leaves offering rich taste and refreshing aroma.', 649.00, 110, 'APPROVED', true),

(3, 6, 'Nescafe Classic Coffee 200g', 'Instant coffee made from carefully selected coffee beans.', 499.00, 90, 'APPROVED', true),

(3, 6, 'Parle-G Biscuits Family Pack', 'Classic glucose biscuits loved by all age groups.', 60.00, 250, 'APPROVED', true),

(3, 6, 'Dabur Pure Honey 500g', '100% pure honey packed with natural goodness and rich taste.', 349.00, 80, 'APPROVED', true),

(3, 6, 'Premium Mixed Dry Fruits 500g', 'Healthy mix of almonds, cashews, raisins and pistachios.', 799.00, 60, 'APPROVED', true),

(3, 6, 'Amul Taaza UHT Milk 1L', 'Fresh toned milk with long shelf life and rich nutritional value.', 72.00, 180, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 7 (BOOKS)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 7, 'Clean Code', 'A practical guide by Robert C. Martin for writing clean, maintainable and professional software.', 799.00, 80, 'APPROVED', true),

(3, 7, 'Effective Java (3rd Edition)', 'Comprehensive guide by Joshua Bloch covering Java best practices and modern programming techniques.', 999.00, 60, 'APPROVED', true),

(3, 7, 'Atomic Habits', 'James Clear explains how small daily habits can produce remarkable long-term results.', 599.00, 100, 'APPROVED', true),

(3, 7, 'Rich Dad Poor Dad', 'Robert Kiyosaki shares valuable lessons on personal finance and wealth creation.', 499.00, 120, 'APPROVED', true),

(3, 7, 'The Pragmatic Programmer', 'Classic software engineering book covering practical programming principles and career growth.', 899.00, 50, 'APPROVED', true),

(3, 7, 'Spring in Action', 'Comprehensive guide to building enterprise applications using the Spring Framework and Spring Boot.', 1199.00, 40, 'APPROVED', true),

(3, 7, 'Head First Design Patterns', 'Easy-to-understand introduction to software design patterns with practical Java examples.', 1099.00, 45, 'APPROVED', true),

(3, 7, 'Deep Work', 'Cal Newport explores focused work techniques for achieving exceptional productivity.', 549.00, 75, 'APPROVED', true),

(3, 7, 'The Alchemist', 'Paulo Coelho''s inspiring novel about dreams, destiny and personal discovery.', 399.00, 90, 'APPROVED', true),

(3, 7, 'Ikigai', 'A bestselling book exploring the Japanese philosophy of living a long, meaningful and happy life.', 449.00, 110, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 8 (SPORTS & FITNESS)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 8, 'Adjustable Dumbbells Set 20kg', 'Premium adjustable dumbbell set suitable for strength training and home workouts.', 4999.00, 30, 'APPROVED', true),

(3, 8, 'Anti-Slip Yoga Mat 6mm', 'Comfortable anti-slip yoga mat with excellent grip for yoga, stretching and meditation.', 999.00, 100, 'APPROVED', true),

(3, 8, 'English Willow Cricket Bat', 'Professional grade English willow cricket bat designed for powerful stroke play.', 6499.00, 25, 'APPROVED', true),

(3, 8, 'FIFA Training Football Size 5', 'Durable football suitable for training sessions and recreational matches.', 1299.00, 80, 'APPROVED', true),

(3, 8, 'Indoor Outdoor Basketball Size 7', 'High-quality basketball with superior grip for indoor and outdoor courts.', 1499.00, 60, 'APPROVED', true),

(3, 8, 'Resistance Bands Set', 'Complete resistance band kit for strength training, mobility and rehabilitation exercises.', 799.00, 120, 'APPROVED', true),

(3, 8, 'Protein Shaker Bottle 700ml', 'Leak-proof BPA-free shaker bottle with mixing ball for protein drinks.', 399.00, 150, 'APPROVED', true),

(3, 8, 'Premium Gym Gloves', 'Breathable padded gym gloves providing excellent grip and wrist support.', 699.00, 90, 'APPROVED', true),

(3, 8, 'Digital Skipping Rope', 'Smart skipping rope with digital calorie and jump counter display.', 899.00, 70, 'APPROVED', true),

(3, 8, 'Foldable Motorized Treadmill', 'Home treadmill with multiple workout modes, LCD display and foldable design.', 32999.00, 12, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 9 (TOYS & GAMES)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 9, 'LEGO Classic Building Blocks Set', 'Creative building block set with hundreds of colorful pieces for endless imagination.', 2499.00, 50, 'APPROVED', true),

(3, 9, 'Remote Control Sports Car', 'High-speed rechargeable RC sports car with 2.4GHz remote control.', 1899.00, 40, 'APPROVED', true),

(3, 9, 'Barbie Dream Doll', 'Beautiful Barbie doll with stylish outfit and accessories for imaginative play.', 1499.00, 60, 'APPROVED', true),

(3, 9, 'Wooden Chess Board Set', 'Premium wooden chess board with finely crafted chess pieces for all skill levels.', 999.00, 45, 'APPROVED', true),

(3, 9, '1000 Piece Jigsaw Puzzle', 'High-quality puzzle featuring a beautiful landscape image for family entertainment.', 799.00, 70, 'APPROVED', true),

(3, 9, 'Nerf Elite Blaster', 'Foam dart blaster with long-range shooting and quick reload mechanism.', 1699.00, 35, 'APPROVED', true),

(3, 9, 'Marvel Superhero Action Figure', 'Collectible superhero action figure with movable joints and detailed design.', 1299.00, 55, 'APPROVED', true),

(3, 9, 'Large Teddy Bear 3 Feet', 'Soft plush teddy bear perfect for gifting and cuddling.', 1799.00, 30, 'APPROVED', true),

(3, 9, 'Educational Learning Laptop Toy', 'Interactive educational toy helping children learn alphabets, numbers and words.', 2199.00, 40, 'APPROVED', true),

(3, 9, 'UNO Card Game', 'Classic family card game that is easy to learn and fun for all ages.', 299.00, 120, 'APPROVED', true);



-- =====================================================
-- PRODUCTS - CATEGORY 10 (AUTOMOTIVE)
-- Merchant ID = 3
-- =====================================================

INSERT INTO products
(merchant_id, category_id, name, description, price, stock, approval_status, active)
VALUES
(3, 10, 'Michelin Digital Tyre Inflator', 'Portable digital tyre inflator with automatic pressure cut-off and LED display.', 2499.00, 40, 'APPROVED', true),

(3, 10, 'Steelbird Full Face Bike Helmet', 'ISI certified full-face helmet with scratch-resistant visor and comfortable padding.', 1899.00, 60, 'APPROVED', true),

(3, 10, 'Car Mobile Phone Holder', '360-degree adjustable dashboard and windshield mobile phone holder.', 599.00, 120, 'APPROVED', true),

(3, 10, 'Microfiber Car Cleaning Kit', 'Complete car cleaning kit including microfiber cloths, sponge and cleaning brush.', 999.00, 80, 'APPROVED', true),

(3, 10, '3D Car Floor Mat Set', 'Premium waterproof floor mat set designed for maximum interior protection.', 2999.00, 35, 'APPROVED', true),

(3, 10, 'Bosch Car Wiper Blade Set', 'High-performance wiper blades providing streak-free visibility in all weather.', 899.00, 70, 'APPROVED', true),

(3, 10, 'Car Vacuum Cleaner 12V', 'Compact high-power vacuum cleaner for quick interior cleaning.', 2199.00, 45, 'APPROVED', true),

(3, 10, 'Premium Leather Seat Covers', 'Universal fit leather seat cover set offering comfort and premium appearance.', 5499.00, 25, 'APPROVED', true),

(3, 10, 'Castrol Power1 Engine Oil 1L', 'High-performance synthetic engine oil for motorcycles ensuring smooth performance.', 749.00, 100, 'APPROVED', true),

(3, 10, 'Car Air Freshener Gel', 'Long-lasting premium fragrance gel that keeps your car interior fresh.', 349.00, 150, 'APPROVED', true);


