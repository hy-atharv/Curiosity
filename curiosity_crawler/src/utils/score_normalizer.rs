

pub fn normalize_score(x: f32, x_min: f32, x_max: f32) -> f32 {
    let min_max_normalization_score = (x - x_min) / (x_max - x_min);
    min_max_normalization_score
}