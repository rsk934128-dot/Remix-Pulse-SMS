const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read messages", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("messages").get());
});

test("Cross-user: Bob cannot read Alice's messages", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("messages").doc("msg1").set({
      id: "msg1",
      threadAddress: "+8801700112233",
      senderAddress: "Me",
      recipientAddress: "+8801700112233",
      body: "Secret SMS",
      isIncoming: false,
      category: "PERSONAL",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("messages").doc("msg1").get());
});

test("Owner: Alice can create and read her own message", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection("users").doc(ALICE_UID).collection("messages").doc("msg1");

  await assertSucceeds(
    docRef.set({
      id: "msg1",
      threadAddress: "+8801700112233",
      senderAddress: "Me",
      recipientAddress: "+8801700112233",
      body: "Hello from Alice",
      isIncoming: false,
      category: "PERSONAL",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(docRef.get());
});

test("Owner: Schema rejection on invalid category", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const docRef = aliceDb.collection("users").doc(ALICE_UID).collection("messages").doc("msg_bad");

  await assertFails(
    docRef.set({
      id: "msg_bad",
      threadAddress: "+8801700112233",
      senderAddress: "Me",
      recipientAddress: "+8801700112233",
      body: "Bad category",
      isIncoming: false,
      category: "INVALID_CAT",
      createdAt: new Date(),
    })
  );
});
