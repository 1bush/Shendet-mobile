import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { StatusBar } from "expo-status-bar";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Text } from "react-native";
import BounceIcon from "./src/components/BounceIcon";

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

// Ikonat cartoon nga karta e shkruese (shtojë në assets/icons/)
const ICON_FILES = {
  food:     require("./assets/food.png"),
  pet:      require("./assets/pet.png"),
  cosmetics: require("./assets/cosmetics.png"),
  asistent:  require("./assets/asistent.png"),
};

const TABS = [
  { name: "Ushqim", icon: ICON_FILES.food, component: FoodScreen, hidden: false },
  { name: "Kafshë", icon: ICON_FILES.pet, component: AnimalFoodScreen, hidden: false },
  { name: "Kozmetikë", icon: ICON_FILES.cosmetics, component: CosmeticsScreen, hidden: false },
  { name: "Asistenti", icon: ICON_FILES.asistent, component: AsistentAI, hidden: false },
  { name: "Më shumë", component: Menu, hidden: false },
  { name: "Faturë", component: SkanimFature, hidden: true },
  { name: "Barnat", component: ModuliBarnas, hidden: true },
  { name: "Harta", component: HartaProdukteve, hidden: true },
  { name: "Profili", component: ProfiletFamiljare, hidden: true },
];

const tabBarIcon = (iconFile) => () => <BounceIcon source={iconFile} size={26} />;

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
                      tabBarIcon: tabBarIcon(t.icon),
                    }
              }
            />
          ))}
        </Tab.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
