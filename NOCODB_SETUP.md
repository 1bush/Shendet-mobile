# NocoDB setup for SHËNDETI-IM

Create these tables in your NocoDB base:

## Products

Required columns: `barcode`, `name`, `brand`, `category`, `ingredients`, `source`, `score`.

## Allergens

Required columns: `name`, `aliases`, `category`, `severity`.

## INCI_Ingredients

Required columns: `name`, `function`, `risk`, `explanation`, `categories`.

Configure the IDs in the untracked `local.properties` file:

```properties
NOCODB_BASE_URL=https://your-nocodb-host/
NOCODB_TOKEN=your-api-token
NOCODB_ORG_ID=your-workspace-id
NOCODB_BASE_ID=your-base-id
NOCODB_PRODUCTS_TABLE_ID=your-products-table-id
NOCODB_ALLERGENS_TABLE_ID=your-allergens-table-id
NOCODB_INCI_TABLE_ID=your-inci-table-id
```

The app keeps NocoDB disabled when any required connection value is empty. It then
continues with OpenFoodFacts/OpenBeautyFacts/FoodData Central and Ollama. Never commit
`local.properties` or the NocoDB token.