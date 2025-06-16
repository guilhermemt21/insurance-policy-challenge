const express = require('express');
const app = express();
const PORT = 3010;

app.get('/api/fraud_check/:clientId', (req, res) => {
  const { clientId } = req.params;
  console.log(`Received fraud check request for clientId: ${clientId}`);

  res.json({
    orderId: "e053467f-bd48-4fd2-9481-75bd4e88ee40",
    customerId: "7c2a27ba-71ef-4dd8-a3cf-5e094316ffd8",
    analyzedAt: "2024-05-10T12:00:00Z",
    classification: "PREFERRED", // opções: "REGULAR", "HIGH_RISK", "PREFERRED", "NO_INFORMATION"
    occurrences: [
      {
        id: "e053467f-bd48-4fd2-9481-75bd4e88ee40",
        productId: 78900069,
        type: "FRAUD",
        description: "Attempted Fraudulent transaction",
        createdAt: "2024-05-10T12:00:00Z",
        updatedAt: "2024-05-10T12:00:00Z"
      },
      {
        id: "f053467f-bd48-4fd2-9481-75bd4e88ee41",
        productId: 104445569,
        type: "SUSPICION",
        description: "Unusual activity flagged for review",
        createdAt: "2024-04-09T14:35:30Z",
        updatedAt: "2024-04-09T14:35:30Z"
      }
    ]
  });
});

app.listen(PORT, () => {
  console.log(`Mock backend listening on port ${PORT}`);
});

