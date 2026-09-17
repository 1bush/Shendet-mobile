import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { StatusBar } from "expo-status-bar";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Text } from "react-native";

// Ekranet kryesore
import FoodScreen from "./src/screens/FoodScreen";
import AnimalFoodScreen from "./src/screens/AnimalFoodScreen";
import CosmeticsScreen from "./src/screens/CosmeticsScreen";
import AsistentAI from "./src/screens/AsistentAI";
import Menu from "./src/screens/Menu";

// Ekranet dytësore (hapen nga "Më shumë")
import SkanimFature from "./src/screens/SkanimFature";
import ModuliBarnas from "./src/screens/ModuliBarnas";
import HartaProdukteve from "./src/screens/HartaProdukteve";
import ProfiletFamiljare from "./src/screens/ProfiletFamiljare";

const Tab = createBottomTabNavigator();

/** Konfigurimi i të gjithë tab-eve — 5 të dukshme + 4 në menunë "Më shumë". */
const TABS = [
  { name: "Ushqim", emoji: "🥗", component: FoodScreen, hidden: false },
  { name: "Kafshë", emoji: "🐾", component: AnimalFoodScreen, hidden: false },
  { name: "Kozmetikë", emoji: "✨", component: CosmeticsScreen, hidden: false },
  { name: "Asistenti", emoji: "🤖", component: AsistentAI, hidden: false },
  { name: "Më shumë", emoji: "➕", component: Menu, hidden: false },
  { name: "Faturë", emoji: "🧾", component: SkanimFature, hidden: true },
  { name: "Barnat", emoji: "💊", component: ModuliBarnas, hidden: true },
  { name: "Harta", emoji: "🗺️", component: HartaProdukteve, hidden: true },
  { name: "Profili", emoji: "👤", component: ProfiletFamiljare, hidden: true },
];

const icona = (emoji) => () => <Text style={{ fontSize: 20 }}>{emoji}</Text>;

export default function App() {
  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <StatusBar style="light" backgroundColor="#1a7a4a" />
        <Tab.Navigator
          screenOptions={{
            headerShown: false,
            tabBarStyle: {
              backgroundColor: "#fff",
              borderTopColor: "#e8f5e9",
              borderTopWidth: 1.5,
              height: 64,
              paddingBottom: 8,
              paddingTop: 6,
            },
            tabBarLabelStyle: { fontWeight: "700", fontSize: 10 },
            tabBarActiveTintColor: "#1a7a4a",
            tabBarInactiveTintColor: "#aaa",
          }}
        >
          {TABS.map((t) => (
            <Tab.Screen
              key={t.name}
              name={t.name}
              component={t.component}
              options={
                t.hidden
                  ? {
                      // Ekrani është i regjistruar dhe hapet me navigate(t.name),
                      // por nuk shfaqet në shiritin e tab-eve.
                      tabBarButton: () => null,
                    }
                  : {
                      tabBarLabel: t.name,
                      tabBarIcon: icona(t.emoji),
                    }
              }
            />
          ))}
        </Tab.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
