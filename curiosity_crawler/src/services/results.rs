use actix_web::{get, web, HttpResponse, Responder};
use crate::AppState;
use crate::dto::request_dto::CrawlRequestData;
use crate::utils::agentic_crawl::agentic_crawl;

#[get("/results")]
pub async fn results(
    query_data: web::Json<CrawlRequestData>,
    state: web::Data<AppState>,
) -> impl Responder {
    let request_data = query_data.into_inner();

    let crawl_results_data_res = agentic_crawl(request_data).await;

    match crawl_results_data_res {
        Ok(crawl_results_data) => {
            HttpResponse::Ok().json(
                crawl_results_data
            )
        }
        Err(e) => {
            HttpResponse::InternalServerError().json(
                e.to_string()
            )
        }
    }
}