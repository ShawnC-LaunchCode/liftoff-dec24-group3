/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
  theme: {
    screens: {
        'sm': '640px',
              // => @media (min-width: 640px) { ... }
        'md': '842px',
              // => @media (min-width: 768px) { ... }
        'lg': '1024px',
             // => @media (min-width: 1024px) { ... }
        'xl': '1280px',
             // => @media (min-width: 1280px) { ... }

    },
    extend: {},
  },
  plugins: [],
}

