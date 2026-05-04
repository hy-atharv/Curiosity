use crate::models::pages::ResultPage;

pub fn rank_pages(mut pages: Vec<ResultPage>) -> Vec<ResultPage> {
    // Rank and sort Pages based on Relevance/Similarity and Credibility
    pages.sort_by(|a, b| {
        let rank_a = a.similarity_score * 0.5 + a.domain_credibility_score * 0.5;
        let rank_b = b.similarity_score * 0.5 + b.domain_credibility_score * 0.5;

        rank_b.total_cmp(&rank_a)
    });

    pages
}