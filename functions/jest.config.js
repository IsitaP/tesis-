/** Configuración de Jest para las pruebas unitarias de Cloud Functions. */
module.exports = {
  testEnvironment: "node",
  roots: ["<rootDir>/test"],
  // Las de integración necesitan el emulador: npm run test:integracion
  testPathIgnorePatterns: ["/node_modules/", "/test/integracion/"],
  transform: {
    "^.+\\.ts$": ["ts-jest", {tsconfig: "tsconfig.test.json"}],
  },
  // Permite importar "./logicaRutas.js" desde el código TypeScript.
  moduleNameMapper: {
    "^(\\.{1,2}/.*)\\.js$": "$1",
  },
};
