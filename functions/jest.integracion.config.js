/**
 * Configuración de Jest para las pruebas de integración de Cloud
 * Functions. Necesitan el emulador de Firestore, por eso se corren con
 * `npm run test:integracion`, que lo levanta y lo apaga al terminar.
 */
const base = require("./jest.config.js");

module.exports = {
  ...base,
  roots: ["<rootDir>/test/integracion"],
  testPathIgnorePatterns: [],
  testTimeout: 30000,
};
