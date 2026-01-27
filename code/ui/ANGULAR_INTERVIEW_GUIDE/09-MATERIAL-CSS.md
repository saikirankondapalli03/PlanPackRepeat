# Angular Interview Guide - Part 9: Material Design CSS

## 🎯 What is Material Design?

Material Design is a **design system** created by Google that provides guidelines for visual, motion, and interaction design. It's based on the metaphor of **paper and ink** - elements have depth, shadows, and can be elevated.

**Key Characteristics:**
- **Elevation** - Elements have shadows to show depth
- **Motion** - Smooth animations and transitions
- **Typography** - Clear hierarchy with Roboto font
- **Color** - Vibrant, meaningful color palettes
- **Grid System** - 8dp grid for spacing

---

## 📊 Material Design vs Bootstrap

### Your Current Project Uses: **Bootstrap 3.3.7**

| Feature | Bootstrap | Material Design |
|---------|-----------|-----------------|
| **Philosophy** | Utility-first, responsive grid | Paper/ink metaphor, elevation |
| **Components** | Pre-built components | Customizable components |
| **Styling** | Classes like `.btn`, `.card` | CSS variables, theming |
| **Icons** | Glyphicons | Material Icons |
| **Grid** | 12-column grid | 8dp/4dp spacing grid |

---

## 1️⃣ ELEVATION - The Foundation of Material

### What is Elevation?

Elevation creates **depth** using shadows. Elements appear to float above the surface.

### Elevation Levels (0-24dp)

```css
/* Material Design Elevation Examples */

/* Elevation 0 - No shadow (flat) */
.elevation-0 {
  box-shadow: none;
}

/* Elevation 2 - Subtle shadow (buttons, cards) */
.elevation-2 {
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
}

/* Elevation 4 - Medium shadow (floating buttons) */
.elevation-4 {
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12),
    0px 1px 5px rgba(0, 0, 0, 0.2);
}

/* Elevation 8 - High shadow (dialogs, modals) */
.elevation-8 {
  box-shadow: 
    0px 4px 5px rgba(0, 0, 0, 0.14),
    0px 1px 10px rgba(0, 0, 0, 0.12),
    0px 2px 4px rgba(0, 0, 0, 0.2);
}

/* Elevation 24 - Highest shadow (dropdowns, tooltips) */
.elevation-24 {
  box-shadow: 
    0px 11px 15px rgba(0, 0, 0, 0.14),
    0px 24px 38px rgba(0, 0, 0, 0.12),
    0px 9px 46px rgba(0, 0, 0, 0.2);
}
```

### When to Use Each Elevation:

- **0dp**: Surfaces (backgrounds, sidebars)
- **2dp**: Cards, buttons at rest
- **4dp**: Floating action buttons (FABs)
- **8dp**: Dialogs, bottom sheets
- **16dp**: Navigation drawers
- **24dp**: Dropdowns, tooltips

### Example: Card with Elevation

```html
<!-- Material Design Card -->
<div class="material-card elevation-2">
  <h3>Card Title</h3>
  <p>Card content goes here</p>
</div>
```

```css
.material-card {
  background: white;
  border-radius: 4px;
  padding: 16px;
  /* Elevation adds depth */
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
}

/* Hover state - increase elevation */
.material-card:hover {
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12),
    0px 1px 5px rgba(0, 0, 0, 0.2);
  transition: box-shadow 0.3s ease;
}
```

---

## 2️⃣ TYPOGRAPHY - Material Design Font System

### Material Design Typography Scale

Material uses **Roboto** font family with specific sizes and weights:

```css
/* Material Typography Classes */

/* Display 4 - Largest (56px) */
.display-4 {
  font-size: 56px;
  font-weight: 400;
  line-height: 1.2;
  letter-spacing: -0.5px;
}

/* Display 3 (45px) */
.display-3 {
  font-size: 45px;
  font-weight: 400;
  line-height: 1.2;
  letter-spacing: 0;
}

/* Headline (24px) - Section headers */
.headline {
  font-size: 24px;
  font-weight: 400;
  line-height: 1.33;
  letter-spacing: 0;
}

/* Title (20px) - Card titles */
.title {
  font-size: 20px;
  font-weight: 500;
  line-height: 1.6;
  letter-spacing: 0.15px;
}

/* Subheading (16px) - Secondary text */
.subheading {
  font-size: 16px;
  font-weight: 400;
  line-height: 1.75;
  letter-spacing: 0.15px;
}

/* Body 1 (16px) - Main body text */
.body-1 {
  font-size: 16px;
  font-weight: 400;
  line-height: 1.5;
  letter-spacing: 0.5px;
}

/* Body 2 (14px) - Secondary body text */
.body-2 {
  font-size: 14px;
  font-weight: 400;
  line-height: 1.43;
  letter-spacing: 0.25px;
}

/* Caption (12px) - Small text, captions */
.caption {
  font-size: 12px;
  font-weight: 400;
  line-height: 1.33;
  letter-spacing: 0.4px;
}

/* Button text (14px) */
.button-text {
  font-size: 14px;
  font-weight: 500;
  line-height: 1.14;
  letter-spacing: 1.25px;
  text-transform: uppercase;
}
```

### Font Weights

- **300**: Light
- **400**: Regular (default)
- **500**: Medium (buttons, emphasis)
- **700**: Bold (headings)

---

## 3️⃣ COLOR SYSTEM - Material Palette

### Material Design Color Palette

Material uses a **500** shade as the primary color, with variations:

```css
/* Material Color Palette Example - Blue */

/* Primary Colors */
.material-blue-50 { background-color: #e3f2fd; }
.material-blue-100 { background-color: #bbdefb; }
.material-blue-200 { background-color: #90caf9; }
.material-blue-300 { background-color: #64b5f6; }
.material-blue-400 { background-color: #42a5f5; }
.material-blue-500 { background-color: #2196f3; } /* Primary */
.material-blue-600 { background-color: #1e88e5; }
.material-blue-700 { background-color: #1976d2; }
.material-blue-800 { background-color: #1565c0; }
.material-blue-900 { background-color: #0d47a1; }

/* Accent Colors */
.material-blue-A100 { background-color: #82b1ff; }
.material-blue-A200 { background-color: #448aff; }
.material-blue-A400 { background-color: #2979ff; }
.material-blue-A700 { background-color: #2962ff; }
```

### Color Usage Guidelines

- **Primary (500)**: Main brand color, buttons, links
- **Dark (700-900)**: Text, emphasis
- **Light (50-200)**: Backgrounds, subtle elements
- **Accent (A200-A700)**: Highlights, call-to-action

### Example: Material Button

```css
.material-button {
  background-color: #2196f3; /* Primary 500 */
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1.25px;
  cursor: pointer;
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
}

.material-button:hover {
  background-color: #1976d2; /* Primary 700 */
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12);
}

.material-button:active {
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
}
```

---

## 4️⃣ SPACING - 8dp Grid System

### Material Design Spacing

Material uses an **8dp (density-independent pixels) grid** for consistent spacing:

```css
/* Material Spacing Scale */

/* 4dp increments for small spacing */
.spacing-4 { margin: 4px; padding: 4px; }
.spacing-8 { margin: 8px; padding: 8px; }
.spacing-12 { margin: 12px; padding: 12px; }
.spacing-16 { margin: 16px; padding: 16px; }

/* 8dp increments for standard spacing */
.spacing-24 { margin: 24px; padding: 24px; }
.spacing-32 { margin: 32px; padding: 32px; }
.spacing-40 { margin: 40px; padding: 40px; }
.spacing-48 { margin: 48px; padding: 48px; }

/* Common Material Spacing */
.material-padding-small { padding: 8px; }
.material-padding-medium { padding: 16px; }
.material-padding-large { padding: 24px; }
.material-padding-xlarge { padding: 32px; }
```

### Component Spacing Examples

```css
/* Material Card Spacing */
.material-card {
  padding: 16px; /* Standard card padding */
  margin: 8px;   /* Space between cards */
}

/* Material Button Spacing */
.material-button {
  padding: 10px 24px; /* Vertical: 10px, Horizontal: 24px */
  margin: 8px;
}

/* Material List Item Spacing */
.material-list-item {
  padding: 16px; /* Standard list item padding */
  min-height: 48px; /* Minimum touch target */
}
```

---

## 5️⃣ BORDER RADIUS - Rounded Corners

### Material Design Border Radius

Material uses **4dp** as the standard border radius:

```css
/* Material Border Radius */

.material-radius-small {
  border-radius: 2px;
}

.material-radius-medium {
  border-radius: 4px; /* Standard */
}

.material-radius-large {
  border-radius: 8px;
}

.material-radius-xlarge {
  border-radius: 16px;
}

/* Fully rounded (for chips, FABs) */
.material-radius-full {
  border-radius: 50%;
}
```

### Example: Material Card

```css
.material-card {
  background: white;
  border-radius: 4px; /* Standard Material radius */
  padding: 16px;
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
}
```

---

## 6️⃣ RIPPLE EFFECT - Material Interactions

### What is Ripple?

Ripple is a **wave animation** that appears when users interact with elements (click, tap).

### CSS-Only Ripple Effect

```css
/* Ripple Container */
.ripple-container {
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

/* Ripple Effect */
.ripple-effect {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  transform: scale(0);
  animation: ripple-animation 0.6s ease-out;
  pointer-events: none;
}

@keyframes ripple-animation {
  to {
    transform: scale(4);
    opacity: 0;
  }
}
```

### JavaScript-Enhanced Ripple

```typescript
// component.ts
export class MaterialButtonComponent {
  createRipple(event: MouseEvent) {
    const button = event.currentTarget as HTMLElement;
    const circle = document.createElement('span');
    const diameter = Math.max(button.clientWidth, button.clientHeight);
    const radius = diameter / 2;

    circle.style.width = circle.style.height = `${diameter}px`;
    circle.style.left = `${event.clientX - button.offsetLeft - radius}px`;
    circle.style.top = `${event.clientY - button.offsetTop - radius}px`;
    circle.classList.add('ripple-effect');

    const ripple = button.getElementsByClassName('ripple-effect')[0];
    if (ripple) {
      ripple.remove();
    }

    button.appendChild(circle);
  }
}
```

```html
<!-- component.html -->
<button class="material-button ripple-container" (click)="createRipple($event)">
  Click Me
</button>
```

---

## 7️⃣ MATERIAL COMPONENTS - Common Patterns

### Material Card

```css
.material-card {
  background: white;
  border-radius: 4px;
  padding: 16px;
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
  transition: box-shadow 0.3s ease;
}

.material-card:hover {
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12),
    0px 1px 5px rgba(0, 0, 0, 0.2);
}
```

### Material Button

```css
.material-button {
  background-color: #2196f3;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1.25px;
  cursor: pointer;
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  min-height: 36px; /* Minimum touch target */
}

.material-button:hover {
  background-color: #1976d2;
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12);
}

.material-button:active {
  box-shadow: 
    0px 1px 2px rgba(0, 0, 0, 0.14),
    0px 1px 3px rgba(0, 0, 0, 0.12);
}

/* Flat Button (no elevation) */
.material-button-flat {
  background: transparent;
  color: #2196f3;
  box-shadow: none;
}

.material-button-flat:hover {
  background: rgba(33, 150, 243, 0.1);
}
```

### Floating Action Button (FAB)

```css
.material-fab {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #2196f3;
  color: white;
  border: none;
  box-shadow: 
    0px 2px 4px rgba(0, 0, 0, 0.14),
    0px 3px 4px rgba(0, 0, 0, 0.12),
    0px 1px 5px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1000;
}

.material-fab:hover {
  box-shadow: 
    0px 4px 5px rgba(0, 0, 0, 0.14),
    0px 1px 10px rgba(0, 0, 0, 0.12),
    0px 2px 4px rgba(0, 0, 0, 0.2);
  transform: scale(1.1);
}
```

### Material Input Field

```css
.material-input {
  position: relative;
  margin-bottom: 24px;
}

.material-input input {
  width: 100%;
  padding: 12px 0;
  border: none;
  border-bottom: 1px solid #757575;
  background: transparent;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.material-input input:focus {
  border-bottom: 2px solid #2196f3;
}

.material-input label {
  position: absolute;
  top: 12px;
  left: 0;
  color: #757575;
  font-size: 16px;
  pointer-events: none;
  transition: all 0.3s ease;
}

.material-input input:focus + label,
.material-input input:not(:placeholder-shown) + label {
  top: -20px;
  font-size: 12px;
  color: #2196f3;
}
```

---

## 8️⃣ ANIMATIONS & TRANSITIONS

### Material Design Motion Principles

1. **Responsive** - Animations respond to user actions
2. **Natural** - Motion feels natural and fluid
3. **Aware** - Elements are aware of their surroundings

### Common Material Animations

```css
/* Standard Material Transition */
.material-transition {
  transition: all 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

/* Material Easing Curves */
.material-standard {
  transition-timing-function: cubic-bezier(0.4, 0.0, 0.2, 1);
}

.material-decelerate {
  transition-timing-function: cubic-bezier(0.0, 0.0, 0.2, 1);
}

.material-accelerate {
  transition-timing-function: cubic-bezier(0.4, 0.0, 1, 1);
}

.material-sharp {
  transition-timing-function: cubic-bezier(0.4, 0.0, 0.6, 1);
}

/* Material Animation Durations */
.material-fast { transition-duration: 150ms; }
.material-normal { transition-duration: 300ms; }
.material-slow { transition-duration: 500ms; }
```

### Example: Material Card Hover Animation

```css
.material-card {
  transition: 
    box-shadow 0.3s cubic-bezier(0.4, 0.0, 0.2, 1),
    transform 0.3s cubic-bezier(0.4, 0.0, 0.2, 1);
}

.material-card:hover {
  transform: translateY(-2px);
  box-shadow: 
    0px 4px 5px rgba(0, 0, 0, 0.14),
    0px 1px 10px rgba(0, 0, 0, 0.12),
    0px 2px 4px rgba(0, 0, 0, 0.2);
}
```

---

## 9️⃣ RESPONSIVE DESIGN - Material Breakpoints

### Material Design Breakpoints

```css
/* Material Design Breakpoints */

/* Extra Small (Mobile) */
@media (max-width: 599px) {
  .material-xs {
    /* Mobile styles */
  }
}

/* Small (Tablet Portrait) */
@media (min-width: 600px) and (max-width: 959px) {
  .material-sm {
    /* Tablet portrait styles */
  }
}

/* Medium (Tablet Landscape) */
@media (min-width: 960px) and (max-width: 1279px) {
  .material-md {
    /* Tablet landscape styles */
  }
}

/* Large (Desktop) */
@media (min-width: 1280px) and (max-width: 1919px) {
  .material-lg {
    /* Desktop styles */
  }
}

/* Extra Large (Large Desktop) */
@media (min-width: 1920px) {
  .material-xl {
    /* Large desktop styles */
  }
}
```

---

## 🔟 IMPLEMENTING MATERIAL CSS IN ANGULAR

### Option 1: Pure CSS (No Framework)

Create your own Material CSS file:

```css
/* material.css */
@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap');

* {
  font-family: 'Roboto', sans-serif;
}

/* Add your Material styles here */
```

### Option 2: Angular Material (Recommended)

Install Angular Material:

```bash
ng add @angular/material
```

This provides:
- Pre-built Material components
- Theming system
- Accessibility features
- Material icons

### Option 3: Material Design for Bootstrap

If you want to keep Bootstrap but add Material styling:

```bash
npm install mdbootstrap
```

---

## 📝 Interview Questions

### 1. What is Material Design?
**Answer:** Material Design is Google's design system based on the metaphor of paper and ink. It emphasizes elevation, motion, typography, and color to create intuitive, beautiful interfaces.

### 2. What is elevation in Material Design?
**Answer:** Elevation uses shadows to create depth, making elements appear to float above the surface. It ranges from 0dp (flat) to 24dp (highest), with different levels used for different component types.

### 3. What is the 8dp grid system?
**Answer:** Material Design uses an 8dp (density-independent pixels) grid for consistent spacing. All spacing values should be multiples of 4dp or 8dp for visual consistency.

### 4. How does Material Design differ from Bootstrap?
**Answer:** 
- Material focuses on elevation and depth (paper/ink metaphor)
- Bootstrap focuses on utility classes and responsive grid
- Material uses 8dp spacing, Bootstrap uses 12-column grid
- Material emphasizes motion and animations more

### 5. What are Material Design's key principles?
**Answer:**
- **Material is the metaphor** - Paper and ink
- **Bold, graphic, intentional** - Clear typography and color
- **Motion provides meaning** - Animations guide users
- **Elevation** - Shadows show depth and hierarchy

---

## ✅ Practice Exercises

1. **Create a Material Card**
   - Use elevation-2
   - Add hover effect with elevation-4
   - Use 16px padding
   - 4px border radius

2. **Style a Material Button**
   - Primary color (#2196f3)
   - Uppercase text
   - Add ripple effect
   - Proper elevation

3. **Build a Material Input**
   - Floating label
   - Bottom border
   - Focus state with primary color

4. **Implement Responsive Material Layout**
   - Use Material breakpoints
   - Adjust spacing for mobile/desktop

---

## 🎯 Key Takeaways

✅ **Elevation** creates depth using shadows (0-24dp)  
✅ **Typography** uses Roboto with specific scales  
✅ **Color** follows a 500-primary system with variations  
✅ **Spacing** uses 8dp grid for consistency  
✅ **Motion** uses cubic-bezier easing curves  
✅ **Components** follow Material guidelines for consistency  

---

**Next Steps:**
- Practice implementing Material components
- Compare Material vs Bootstrap in your project
- Consider migrating to Angular Material for full Material support
