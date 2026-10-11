/**
 * Ikonë cartoon me bounce kur preket (si Yuka — reagon me kërcim).
 * Përdorje: <BounceIcon source={...} size={26} style={...} />
 * Pa varësi të jashtme — vetëm Animated API i React Native.
 */
import React, { useRef } from "react";
import { Animated, TouchableWithoutFeedback } from "react-native";

export default function BounceIcon({ source, size = 26, style, onPress }) {
  const scale = useRef(new Animated.Value(1)).current;

  function bounce() {
    Animated.sequence([
      Animated.timing(scale, { toValue: 0.7, duration: 90, useNativeDriver: true }),
      Animated.spring(scale, {
        toValue: 1,
        friction: 3,
        tension: 120,
        useNativeDriver: true,
      }),
    ]).start();
    if (onPress) onPress();
  }

  return (
    <TouchableWithoutFeedback onPress={bounce}>
      <Animated.Image
        source={source}
        style={[{ width: size, height: size, transform: [{ scale }] }, style]}
      />
    </TouchableWithoutFeedback>
  );
}
