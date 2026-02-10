export type PriceRange = "$" | "$$" | "$$$";
export type Poi = {
  id: string;
  name: string;
  category: string;
  address: string;
  rating: number; // 0..5
  price: PriceRange;
  openNow: boolean;
  distanceMi: number;
};

export const mockPois: Poi[] = [
  {
    id: "p1",
    name: "Sunset Coffee Lab",
    category: "Cafe",
    address: "123 Citrus Ave",
    rating: 4.6,
    price: "$$",
    openNow: true,
    distanceMi: 0.8
  },
  {
    id: "p2",
    name: "Golden Dragon Noodles",
    category: "Restaurant",
    address: "88 Lantern St",
    rating: 4.3,
    price: "$$",
    openNow: false,
    distanceMi: 2.1
  },
  {
    id: "p3",
    name: "Ridgeview Bookstore",
    category: "Shop",
    address: "41 Maple Blvd",
    rating: 4.8,
    price: "$",
    openNow: true,
    distanceMi: 1.4
  },
  {
    id: "p4",
    name: "Lakefront Park",
    category: "Park",
    address: "500 Shoreline Dr",
    rating: 4.5,
    price: "$",
    openNow: true,
    distanceMi: 3.7
  }
];
