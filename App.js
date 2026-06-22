import { NavigationContainer } from "@react-navigation/native";
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs";
import { StatusBar } from "expo-status-bar";
import { SafeAreaProvider } from "react-native-safe-area-context";
import { Text } from "react-native";

import FoodScreen from "./src/screens/FoodScreen";
import AnimalFoodScreen from "./src/screens/AnimalFoodScreen";
import CosmeticsScreen from "./src/screens/CosmeticsScreen";

const Tab = createBottomTabNavigator();

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
            tabBarLabelStyle: {
              fontWeight: "700",
              fontSize: 11,
            },
            tabBarActiveTintColor: "#1a7a4a",
            tabBarInactiveTintColor: "#aaa",
          }}
        >
          <Tab.Screen
            name="Ushqim"
            component={FoodScreen}
            options={{
              tabBarLabel: "Ushqim",
              tabBarIcon: ({ color }) => (
                <Text style={{ fontSize: 22 }}>🥗</Text>
              ),
            }}
          />
          <Tab.Screen
            name="Kafshë"
            component={AnimalFoodScreen}
            options={{
              tabBarLabel: "Kafshë",
              tabBarIcon: ({ color }) => (
                <Text style={{ fontSize: 22 }}>🐾</Text>
              ),
            }}
          />
          <Tab.Screen
            name="Kozmetikë"
            component={CosmeticsScreen}
            options={{
              tabBarLabel: "Kozmetikë",
              tabBarIcon: ({ color }) => (
                <Text style={{ fontSize: 22 }}>✨</Text>
              ),
            }}
          />
        </Tab.Navigator>
      </NavigationContainer>
    </SafeAreaProvider>
  );
}
