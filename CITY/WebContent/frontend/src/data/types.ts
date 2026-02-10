export type Poi = {
  id: string
  name: string
  category: string
  city: string
  price: '$' | '$$' | '$$$' | '$$$$'
  rating: number
  distanceMi: number
}